package com.kendohamster;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class VideoFragment2 extends Fragment {
    VideoView videoView;
    TextView textView;

    public VideoFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video2, container, false);

        videoView = view.findViewById(R.id.videoView);

        // Your Video URL
        String videoUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1";

        // Uri object to refer the
        // resource from the videoUrl
        Uri uri = Uri.parse(videoUrl);

        Bundle bundle = getArguments();
        int i = bundle.getInt("i",0);

        textView = view.findViewById(R.id.VideoTitle);
        if(i==0){
            textView.setText("分解動作一");
            // sets the resource from the
            // videoUrl to the videoView
            videoView.setVideoURI(uri);
            // creating object of
            // media controller class
            //MediaController mediaController = new MediaController();
            MediaController mediaController = new MediaController(videoView.getContext());
            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoView);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoView);

            // sets the media controller to the videoView
            videoView.setMediaController(mediaController);

            // starts the video
            videoView.start();
        }
        else if(i==1){
            textView.setText("分解動作二");
            // sets the resource from the
            // videoUrl to the videoView
            videoView.setVideoURI(uri);

            // creating object of
            // media controller class
            MediaController mediaController = new MediaController(videoView.getContext());

            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoView);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoView);

            // sets the media controller to the videoView
            videoView.setMediaController(mediaController);

            // starts the video
            videoView.start();
        }
        else if(i==2){
            textView.setText("分解動作三");
            // sets the resource from the
            // videoUrl to the videoView
            videoView.setVideoURI(uri);

            // creating object of
            // media controller class
            MediaController mediaController = new MediaController(videoView.getContext());

            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoView);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoView);

            // sets the media controller to the videoView
            videoView.setMediaController(mediaController);

            // starts the video
            videoView.start();
        }
        else if(i==3){
            textView.setText("分解動作四");
            // sets the resource from the
            // videoUrl to the videoView
            videoView.setVideoURI(uri);
            // creating object of
            // media controller class
            MediaController mediaController = new MediaController(videoView.getContext());

            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoView);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoView);

            // sets the media controller to the videoView
            videoView.setMediaController(mediaController);

            // starts the video
            videoView.start();
        }


        return view;
    }
}