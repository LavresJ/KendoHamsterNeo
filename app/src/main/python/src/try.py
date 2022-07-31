#!/usr/bin/env python
# coding: utf-8

'''
Test action recognition on
(1) a video, (2) a folder of images, (3) or web camera.

Input:
    model: model/trained_classifier.pickle

Output:
    result video:    output/${video_name}/video.avi
    result skeleton: output/${video_name}/skeleton_res/XXXXX.txt
    visualization by cv2.imshow() in img_displayer
'''

'''
Example of usage:

(1) Test on video file:
python src/s5_test.py \
    --model_path model/trained_classifier.pickle \
    --data_type video \
    --data_path data_test/exercise.avi \
    --output_folder output

(2) Test on a folder of images:
python src/s5_test.py \
    --model_path model/trained_classifier.pickle \
    --data_type folder \
    --data_path data_test/apple/ \
    --output_folder output

(3) Test on web camera:
python src/s5_test.py \
    --model_path model/trained_classifier.pickle \
    --data_type webcam \
    --data_path 0 \
    --output_folder output

'''

import numpy as np
from java import (jarray, jdouble, jfloat)
#import cv2
import argparse
from os.path import dirname, join

if True:  # Include project path
    import sys
    import os

    ROOT = os.path.dirname(os.path.abspath(__file__)) + "/../"
    CURR_PATH = os.path.dirname(os.path.abspath(__file__)) + "/"
    sys.path.append(ROOT)

    #import utils.lib_images_io as lib_images_io
    #import utils.lib_plot as lib_plot
    import utils.lib_commons as lib_commons
    #from utils.lib_openpose import SkeletonDetector
    from utils.lib_tracker import Tracker
    from utils.lib_classifier import ClassifierOnlineTest
    from utils.lib_classifier import *  # Import all sklearn related libraries


def par(path):  # Pre-Append ROOT to the path if it's not absolute
    return ROOT + path if (path and path[0] != "/") else path


# -- Command-line input


def get_command_line_arguments():
    def parse_args():
        parser = argparse.ArgumentParser(
            description="Test action recognition on \n"
                        "(1) a video, (2) a folder of images, (3) or web camera.")
        """
        parser.add_argument("-m", "--model_path", required=False,
                            default='model/trained_classifier.pickle')
        """
        pkl_path = join(dirname(__file__), "../model/trained_classifier.pickle")
        parser.add_argument("-m", "--model_path", required=False,
                            default=pkl_path)
        parser.add_argument("-t", "--data_type", required=False, default='webcam',
                            choices=["video", "folder", "webcam"])
        parser.add_argument("-p", "--data_path", required=False, default="",
                            help="path to a video file, or images folder, or webcam. \n"
                                 "For video and folder, the path should be "
                                 "absolute or relative to this project's root. "
                                 "For webcam, either input an index or device name. ")
        parser.add_argument("-o", "--output_folder", required=False, default='output/',
                            help="Which folder to save result to.")

        args = parser.parse_args()
        return args

    args = parse_args()
    if args.data_type != "webcam" and args.data_path and args.data_path[0] != "/":
        # If the path is not absolute, then its relative to the ROOT.
        args.data_path = ROOT + args.data_path
    return args

def get_dst_folder_name(src_data_type, src_data_path):
    ''' Compute a output folder name based on data_type and data_path.
        The final output of this script looks like this:
            DST_FOLDER/folder_name/vidoe.avi
            DST_FOLDER/folder_name/skeletons/XXXXX.txt
    '''

    assert (src_data_type in ["video", "folder", "webcam"])

    if src_data_type == "video":  # /root/data/video.avi --> video
        folder_name = os.path.basename(src_data_path).split(".")[-2]

    elif src_data_type == "folder":  # /root/data/video/ --> video
        folder_name = src_data_path.rstrip("/").split("/")[-1]

    elif src_data_type == "webcam":
        # month-day-hour-minute-seconds, e.g.: 02-26-15-51-12
        folder_name = lib_commons.get_time_string()

    return folder_name

args = get_command_line_arguments()

SRC_DATA_TYPE = args.data_type
SRC_DATA_PATH = args.data_path
SRC_MODEL_PATH = args.model_path

DST_FOLDER_NAME = get_dst_folder_name(SRC_DATA_TYPE, SRC_DATA_PATH)

# -- Settings

cfg_all = lib_commons.read_yaml(ROOT + "config/config.yaml")
cfg = cfg_all["s5_test.py"]

CLASSES = np.array(cfg_all["classes"])
SKELETON_FILENAME_FORMAT = cfg_all["skeleton_filename_format"]

# Action recognition: number of frames used to extract features.
WINDOW_SIZE = int(cfg_all["features"]["window_size"])

# Output folder
DST_FOLDER = args.output_folder + "/" + DST_FOLDER_NAME + "/"
DST_SKELETON_FOLDER_NAME = cfg["output"]["skeleton_folder_name"]
DST_VIDEO_NAME = cfg["output"]["video_name"]
# framerate of output video.avi
DST_VIDEO_FPS = float(cfg["output"]["video_fps"])

# Video setttings

# If data_type is webcam, set the max frame rate.
SRC_WEBCAM_MAX_FPS = float(cfg["settings"]["source"]
                           ["webcam_max_framerate"])

# If data_type is video, set the sampling interval.
# For example, if it's 3, then the video will be read 3 times faster.
SRC_VIDEO_SAMPLE_INTERVAL = int(cfg["settings"]["source"]
                                ["video_sample_interval"])

# Openpose settings
OPENPOSE_MODEL = cfg["settings"]["openpose"]["model"]
OPENPOSE_IMG_SIZE = cfg["settings"]["openpose"]["img_size"]

# Display settings
img_disp_desired_rows = int(cfg["settings"]["display"]["desired_rows"])


# -- Function


class MultiPersonClassifier(object):
    ''' This is a wrapper around ClassifierOnlineTest
        for recognizing actions of multiple people.
    '''
    mean_score = []

    def __init__(self, model_path, classes):

        self.dict_id2clf = {}  # human id -> classifier of this person

        # Define a function for creating classifier for new people.
        self._create_classifier = lambda human_id: ClassifierOnlineTest(
            model_path, classes, WINDOW_SIZE, human_id)

    def classify(self, dict_id2skeleton):
        ''' Classify the action type of each skeleton in dict_id2skeleton '''

        # Clear people not in view
        old_ids = set(self.dict_id2clf)
        cur_ids = set(dict_id2skeleton)
        humans_not_in_view = list(old_ids - cur_ids)
        for human in humans_not_in_view:
            del self.dict_id2clf[human]

        # Predict each person's action
        id2label = {}
        for id, skeleton in dict_id2skeleton.items():

            if id not in self.dict_id2clf:  # add this new person
                self.dict_id2clf[id] = self._create_classifier(id)

            classifier = self.dict_id2clf[id]
            id2label[id] = classifier.predict(skeleton)  # predict label
            # print("\n\nPredicting label for human{}".format(id))
            # print("  skeleton: {}".format(skeleton))
            # print("  label: {}".format(id2label[id]))
            MultiPersonClassifier.mean_score = classifier.mean_score

        return id2label

    def get_classifier(self, id):
        ''' Get the classifier based on the person id.
        Arguments:
            id {int or "min"}
        '''
        if len(self.dict_id2clf) == 0:
            return None
        if id == 'min':
            id = min(self.dict_id2clf.keys())
        return self.dict_id2clf[id]


def remove_skeletons_with_few_joints(skeletons):
    ''' Remove bad skeletons before sending to the tracker '''
    good_skeletons = []
    for skeleton in skeletons:
        px = skeleton[2:2 + 13 * 2:2]
        py = skeleton[3:2 + 13 * 2:2]
        num_valid_joints = len([x for x in px if x != 0])
        num_leg_joints = len([x for x in px[-6:] if x != 0])
        total_size = max(py) - min(py)
        # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        # IF JOINTS ARE MISSING, TRY CHANGING THESE VALUES:
        # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if num_valid_joints >= 5 and total_size >= 0.1 and num_leg_joints >= 0:
            # add this skeleton only when all requirements are satisfied
            good_skeletons.append(skeleton)
    return good_skeletons


def get_the_skeleton_data_to_save_to_disk(dict_id2skeleton):
    '''
    In each image, for each skeleton, save the:
        human_id, label, and the skeleton positions of length 18*2.
    So the total length per row is 2+36=38
    '''
    skels_to_save = []
    for human_id in dict_id2skeleton.keys():
        label = dict_id2label[human_id]
        skeleton = dict_id2skeleton[human_id]
        skels_to_save.append([[human_id, label] + skeleton.tolist()])
    return skels_to_save


# -- Main
#if __name__ == "__main__":
"""
def main():
    sk1 = [[0, 0, 0.4573170731707317, 0.17119565217391303, 0.5457317073170732, 0.17527173913043478, 0.6158536585365854, 0.35054347826086957, 0, 0, 0.3597560975609756, 0.14266304347826086, 0.3384146341463415, 0.30978260869565216, 0.31097560975609756, 0.4891304347826087, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.4817073170731707, 0.028532608695652176, 0.38109756097560976, 0.024456521739130432]]
    sk2 = [[0, 0, 0.4298780487804878, 0.19157608695652176, 0.5182926829268293, 0.1875, 0.5884146341463414, 0.3586956521739131, 0.5792682926829268, 0.5135869565217391, 0.3353658536585366, 0.17119565217391303, 0.31097560975609756, 0.30570652173913043, 0.29878048780487804, 0.47282608695652173, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.46646341463414637, 0.06521739130434782, 0.3719512195121951, 0.05706521739130435]]
    sk3 = [[0, 0, 0.4176829268292683, 0.21195652173913043, 0.5060975609756098, 0.19972826086956524, 0.5670731707317073, 0.3586956521739131, 0.551829268292683, 0.4891304347826087, 0.3201219512195122, 0.19565217391304346, 0.3018292682926829, 0.30978260869565216, 0.2804878048780488, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0.45121951219512196, 0.08559782608695651, 0.36585365853658536, 0.08152173913043478]]
    sk4 = [[0, 0, 0.3871951219512195, 0.23233695652173914, 0.47560975609756095, 0.21603260869565216, 0.5182926829268293, 0.3586956521739131, 0.5274390243902439, 0.4891304347826087, 0.2896341463414634, 0.23233695652173914, 0.27134146341463417, 0.3586956521739131, 0.25914634146341464, 0.5054347826086957, 0.4481707317073171, 0.47282608695652173, 0.4481707317073171, 0.65625, 0, 0, 0.3628048780487805, 0.4483695652173913, 0, 0, 0, 0, 0, 0, 0, 0, 0.42073170731707316, 0.1141304347826087, 0.34146341463414637, 0.11820652173913043]]
    sk5 = [[0, 0, 0.3719512195121951, 0.22418478260869565, 0.4573170731707317, 0.21603260869565216, 0.4969512195121951, 0.34239130434782605, 0.524390243902439, 0.46875, 0.28353658536585363, 0.2282608695652174, 0, 0, 0, 0, 0.4115853658536585, 0.46467391304347827, 0, 0, 0, 0, 0.3353658536585366, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0.4115853658536585, 0.11820652173913043, 0.3353658536585366, 0.12635869565217392]]
    sk6 = [[0, 0, 0.3597560975609756, 0.22418478260869565, 0.4451219512195122, 0.22010869565217395, 0.47865853658536583, 0.3342391304347826, 0.5152439024390244, 0.4605978260869565, 0.2804878048780488, 0.22418478260869565, 0.24695121951219512, 0.3586956521739131, 0, 0, 0.4146341463414634, 0.47282608695652173, 0.35365853658536583, 0.6603260869565217, 0, 0, 0.31402439024390244, 0.46875, 0, 0, 0, 0, 0, 0, 0, 0, 0.40853658536585363, 0.12635869565217392, 0.3323170731707317, 0.13043478260869565]]
    sk7 = [[0, 0, 0.35060975609756095, 0.2404891304347826, 0.4329268292682927, 0.2404891304347826, 0.4573170731707317, 0.35054347826086957, 0.4878048780487805, 0.4565217391304348, 0.2774390243902439, 0.23641304347826086, 0.2621951219512195, 0.33016304347826086, 0, 0, 0.3932926829268293, 0.4850543478260869, 0.3597560975609756, 0.6317934782608695, 0.35060975609756095, 0.7133152173913043, 0.3048780487804878, 0.46875, 0.32621951219512196, 0.6317934782608695, 0, 0, 0, 0, 0, 0, 0.4054878048780488, 0.15489130434782608, 0.3353658536585366, 0.15489130434782608]]
    sk8 = [[0, 0, 0.3475609756097561, 0.25271739130434784, 0.4176829268292683, 0.2608695652173913, 0.43597560975609756, 0.375, 0.4634146341463415, 0.4891304347826087, 0.2804878048780488, 0.25271739130434784, 0.25914634146341464, 0.3342391304347826, 0, 0, 0.3780487804878049, 0.4932065217391305, 0.3475609756097561, 0.6358695652173914, 0, 0, 0.2926829268292683, 0.4605978260869565, 0.2926829268292683, 0.6154891304347826, 0, 0, 0, 0, 0, 0, 0.39939024390243905, 0.17527173913043478, 0.3353658536585366, 0.16304347826086957]]
    sk9 = [[0.4024390243902439, 0.1875, 0.34146341463414637, 0.24864130434782605, 0.4054878048780488, 0.24456521739130435, 0.4115853658536585, 0.3586956521739131, 0.4329268292682927, 0.4565217391304348, 0.28353658536585363, 0.24864130434782605, 0, 0, 0, 0, 0.36585365853658536, 0.47282608695652173, 0.3445121951219512, 0.6195652173913043, 0, 0, 0.29573170731707316, 0.4565217391304348, 0.3079268292682927, 0.5991847826086957, 0, 0, 0.4024390243902439, 0.17934782608695654, 0, 0, 0.3902439024390244, 0.17527173913043478, 0.3353658536585366, 0.16304347826086957]]

    sk_arr = [sk1, sk2, sk3, sk4, sk5, sk6, sk7, sk8, sk9]
    # -- Detector, tracker, classifier

    ###
    ###
    ###
    multiperson_tracker = Tracker()

    multiperson_classifier = MultiPersonClassifier(SRC_MODEL_PATH, CLASSES)

    i = 1
    predict_label = []
    for sk in sk_arr:
        print("\nsk" + str(i) + ": " + str(sk))

        # -- Track people
        # dict_id2skeleton: 把骨架x, y四捨五入加上人的id
        dict_id2skeleton = multiperson_tracker.track(
            sk)  # int id -> np.array() skeleton

        print("\ndict_id2skeleton: " + str(dict_id2skeleton))

        # -- Recognize action of each person
        # dict_id2label: 存預測動作結果的label
        if len(dict_id2skeleton):
            dict_id2label = multiperson_classifier.classify(
                dict_id2skeleton)

        # Print label of a person
        if len(dict_id2skeleton):
            min_id = min(dict_id2skeleton.keys())
            print("prediced label(sk" + str(i) + ") is :", dict_id2label[min_id])
            predict_label.append(dict_id2label[min_id])
        i = i+1

    return  predict_label
"""
    ###
    ###
    ###

multiperson_tracker = Tracker()

multiperson_classifier = MultiPersonClassifier(SRC_MODEL_PATH, CLASSES)

def main(sk):

    # -- Detector, tracker, classifier

    ###
    ###
    ###

    predict_label = ""

    #print("\nsk: " + str(sk))
    #print("sk[0] = " + str(sk[0]))
    #print("sk[1] = " + str(sk[1]))
    #print("sk[2] = " + str(sk[2]))

    sk = list(jarray(jfloat)(sk))
    sk = [sk]
    #print("sk = " + str(sk))

    # -- Track people
    # dict_id2skeleton: 把骨架x, y四捨五入加上人的id
    dict_id2skeleton = multiperson_tracker.track(
        sk)  # int id -> np.array() skeleton

    #print("\ndict_id2skeleton: " + str(dict_id2skeleton))

    # -- Recognize action of each person
    # dict_id2label: 存預測動作結果的label

    if len(dict_id2skeleton):
        dict_id2label = multiperson_classifier.classify(
            dict_id2skeleton)

    # Print label of a person
    if len(dict_id2skeleton):
        min_id = min(dict_id2skeleton.keys())
        print("prediced label(sk) is :", dict_id2label[min_id])
        predict_label = dict_id2label[min_id]

    mean_score_list = multiperson_classifier.mean_score
    return mean_score_list
    #return  predict_label