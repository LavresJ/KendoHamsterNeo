/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package com.kendohamster.ml

import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.Log
import com.chaquo.python.PyObject
import com.kendohamster.*
import com.kendohamster.data.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

enum class ModelType {
    Lightning,
    Thunder
}



class MoveNet(private val interpreter: Interpreter, private var gpuDelegate: GpuDelegate?) :
    PoseDetector {

    companion object {
        private const val MIN_CROP_KEYPOINT_SCORE = .2f
        private const val CPU_NUM_THREADS = 4

        // Parameters that control how large crop region should be expanded from previous frames'
        // body keypoints.
        private const val TORSO_EXPANSION_RATIO = 1.9f
        private const val BODY_EXPANSION_RATIO = 1.2f

        // TFLite file names.
        private const val LIGHTNING_FILENAME = "movenet_lightning.tflite"
        private const val THUNDER_FILENAME = "movenet_thunder.tflite"

        // Python object
        private lateinit var pyobj:PyObject

        // allow specifying model type.
        fun create(context: Context, device: Device, modelType: ModelType, pyobj: PyObject): MoveNet {
            this.pyobj = pyobj

            val options = Interpreter.Options()
            var gpuDelegate: GpuDelegate? = null
            options.setNumThreads(CPU_NUM_THREADS)
            when (device) {
                Device.CPU -> {
                }
                Device.GPU -> {
                    gpuDelegate = GpuDelegate()
                    options.addDelegate(gpuDelegate)
                }
                Device.NNAPI -> options.setUseNNAPI(true)
            }
            return MoveNet(
                Interpreter(
                    FileUtil.loadMappedFile(
                        context,
                        if (modelType == ModelType.Lightning) LIGHTNING_FILENAME
                        else THUNDER_FILENAME
                    ), options
                ),
                gpuDelegate
            )
        }

        // default to lightning.
        fun create(context: Context, device: Device, pyobj: PyObject): MoveNet =
            create(context, device, ModelType.Lightning, pyobj)
    }

    private var cropRegion: RectF? = null
    private var lastInferenceTimeNanos: Long = -1
    private val inputWidth = interpreter.getInputTensor(0).shape()[1]
    private val inputHeight = interpreter.getInputTensor(0).shape()[2]
    private var outputShape: IntArray = interpreter.getOutputTensor(0).shape()
    private var scale_h = 640 / 480.toFloat()
    private var skeleton = DoubleArray(36)

    override fun estimatePoses(bitmap: Bitmap): List<Person> {
        Log.d("scale_h", scale_h.toString())
        val inferenceStartTimeNanos = SystemClock.elapsedRealtimeNanos()
        if (cropRegion == null) {
            cropRegion = initRectF(bitmap.width, bitmap.height)
        }
        var totalScore = 0f

        val numKeyPoints = outputShape[2]
        val keyPoints = mutableListOf<KeyPoint>()
        val keyPoints_float = mutableListOf<KeyPoint>()

        cropRegion?.run {
            val rect = RectF(
                (left * bitmap.width),
                (top * bitmap.height),
                (right * bitmap.width),
                (bottom * bitmap.height)
            )
            val detectBitmap = Bitmap.createBitmap(
                rect.width().toInt(),
                rect.height().toInt(),
                Bitmap.Config.ARGB_8888
            )
            Canvas(detectBitmap).drawBitmap(
                bitmap,
                -rect.left,
                -rect.top,
                null
            )
            val inputTensor = processInputImage(detectBitmap, inputWidth, inputHeight)
            val outputTensor = TensorBuffer.createFixedSize(outputShape, DataType.FLOAT32)
            val widthRatio = detectBitmap.width.toFloat() / inputWidth
            val heightRatio = detectBitmap.height.toFloat() / inputHeight

            val positions = mutableListOf<Float>()

            inputTensor?.let { input ->
                interpreter.run(input.buffer, outputTensor.buffer.rewind())
                val output = outputTensor.floatArray
                for (idx in 0 until numKeyPoints) {
                    val x = output[idx * 3 + 1] * inputWidth * widthRatio
                    val y = output[idx * 3 + 0] * inputHeight * heightRatio

                    positions.add(x)
                    positions.add(y)
                    val score = output[idx * 3 + 2]
                    keyPoints.add(
                        KeyPoint(
                            BodyPart.fromInt(idx),
                            PointF(
                                x,
                                y
                            ),
                            score
                        )
                    )

                    keyPoints_float.add(
                        KeyPoint(
                            BodyPart.fromInt(idx),
                            PointF(
                                x,
                                y
                            ),
                            score
                        )
                    )

                    totalScore += score
                }
            }
            val matrix = Matrix()
            val points = positions.toFloatArray()

            matrix.postTranslate(rect.left, rect.top)
            matrix.mapPoints(points)
            keyPoints.forEachIndexed { index, keyPoint ->
                keyPoint.coordinate =
                    PointF(
                        points[index * 2],
                        points[index * 2 + 1]
                    )
            }
            keyPoints_float.forEachIndexed { index, keyPoint ->
                keyPoint.coordinate =
                    PointF(
                        points[index * 2] / 480,
                        points[index * 2 + 1] / 640 * scale_h
                    )
            }
            // new crop region
            cropRegion = determineRectF(keyPoints, bitmap.width, bitmap.height)
        }
        lastInferenceTimeNanos =
            SystemClock.elapsedRealtimeNanos() - inferenceStartTimeNanos

        /////////////
        //判斷手腕是否高於肩膀(數字小的較高)

        val rightShoulder = keyPoints[6].coordinate.y
        val rightWrist = keyPoints[10].coordinate.y
        if(rightShoulder != null && rightWrist != null){
            wristAboveShoulder = rightWrist < rightShoulder
            if(lastBoolean != wristAboveShoulder){
                count += 0.5
            }
            lastBoolean = wristAboveShoulder
            Log.d("ESTI", "揮劍次數:"+ count)
        }
        /////判斷腳步步數
        val rightAnkle = keyPoints[16].coordinate.x
        val leftAnkle = keyPoints[15].coordinate.x
        if(rightAnkle != null && leftAnkle != null){
            if(leftAnkle - rightAnkle > 100){
                AnkleStep = true
            }
            else{
                AnkleStep = false
            }
            if(lastAnkleStep != AnkleStep){
                stepCount += 0.5
            }
            lastAnkleStep = AnkleStep
            Log.d("123456", "腳步移動次數:"+ stepCount)
        }

        //Log.d("left_wtist", "x:" + keyPoints[9].coordinate.x.toString() + " y:" + keyPoints[9].coordinate.y.toString())
        Log.d("keyPoints", keyPoints.toString())
        Log.d("keyPoints_float", keyPoints_float.toString())

        skeleton = keyPoints_to_skeleton(keyPoints_float)
        Log.d("skeleton", skeleton.contentToString())
        val obj = pyobj.callAttr("main", skeleton)
        Log.d("result", obj.toString())

        return listOf(Person(keyPoints = keyPoints, score = totalScore / numKeyPoints))
    }

    override fun lastInferenceTimeNanos(): Long = lastInferenceTimeNanos

    override fun close() {
        gpuDelegate?.close()
        interpreter.close()
        cropRegion = null
    }

    /**
     * Prepare input image for detection
     */
    private fun processInputImage(bitmap: Bitmap, inputWidth: Int, inputHeight: Int): TensorImage? {
        val width: Int = bitmap.width
        val height: Int = bitmap.height

        val size = if (height > width) width else height
        val imageProcessor = ImageProcessor.Builder().apply {
            add(ResizeWithCropOrPadOp(size, size))
            add(ResizeOp(inputWidth, inputHeight, ResizeOp.ResizeMethod.BILINEAR))
        }.build()
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        return imageProcessor.process(tensorImage)
    }

    /**
     * Defines the default crop region.
     * The function provides the initial crop region (pads the full image from both
     * sides to make it a square image) when the algorithm cannot reliably determine
     * the crop region from the previous frame.
     */
    private fun initRectF(imageWidth: Int, imageHeight: Int): RectF {
        val xMin: Float
        val yMin: Float
        val width: Float
        val height: Float
        if (imageWidth > imageHeight) {
            width = 1f
            height = imageWidth.toFloat() / imageHeight
            xMin = 0f
            yMin = (imageHeight / 2f - imageWidth / 2f) / imageHeight
        } else {
            height = 1f
            width = imageHeight.toFloat() / imageWidth
            yMin = 0f
            xMin = (imageWidth / 2f - imageHeight / 2) / imageWidth
        }
        return RectF(
            xMin,
            yMin,
            xMin + width,
            yMin + height
        )
    }

    /**
     * Checks whether there are enough torso keypoints.
     * This function checks whether the model is confident at predicting one of the
     * shoulders/hips which is required to determine a good crop region.
     */
    private fun torsoVisible(keyPoints: List<KeyPoint>): Boolean {
        return ((keyPoints[BodyPart.LEFT_HIP.position].score > MIN_CROP_KEYPOINT_SCORE).or(
            keyPoints[BodyPart.RIGHT_HIP.position].score > MIN_CROP_KEYPOINT_SCORE
        )).and(
            (keyPoints[BodyPart.LEFT_SHOULDER.position].score > MIN_CROP_KEYPOINT_SCORE).or(
                keyPoints[BodyPart.RIGHT_SHOULDER.position].score > MIN_CROP_KEYPOINT_SCORE
            )
        )
    }

    /**
     * Determines the region to crop the image for the model to run inference on.
     * The algorithm uses the detected joints from the previous frame to estimate
     * the square region that encloses the full body of the target person and
     * centers at the midpoint of two hip joints. The crop size is determined by
     * the distances between each joints and the center point.
     * When the model is not confident with the four torso joint predictions, the
     * function returns a default crop which is the full image padded to square.
     */
    private fun determineRectF(
        keyPoints: List<KeyPoint>,
        imageWidth: Int,
        imageHeight: Int
    ): RectF {
        val targetKeyPoints = mutableListOf<KeyPoint>()
        keyPoints.forEach {
            targetKeyPoints.add(
                KeyPoint(
                    it.bodyPart,
                    PointF(
                        it.coordinate.x,
                        it.coordinate.y
                    ),
                    it.score
                )
            )
        }
        if (torsoVisible(keyPoints)) {
            val centerX =
                (targetKeyPoints[BodyPart.LEFT_HIP.position].coordinate.x +
                        targetKeyPoints[BodyPart.RIGHT_HIP.position].coordinate.x) / 2f
            val centerY =
                (targetKeyPoints[BodyPart.LEFT_HIP.position].coordinate.y +
                        targetKeyPoints[BodyPart.RIGHT_HIP.position].coordinate.y) / 2f

            val torsoAndBodyDistances =
                determineTorsoAndBodyDistances(keyPoints, targetKeyPoints, centerX, centerY)

            val list = listOf(
                torsoAndBodyDistances.maxTorsoXDistance * TORSO_EXPANSION_RATIO,
                torsoAndBodyDistances.maxTorsoYDistance * TORSO_EXPANSION_RATIO,
                torsoAndBodyDistances.maxBodyXDistance * BODY_EXPANSION_RATIO,
                torsoAndBodyDistances.maxBodyYDistance * BODY_EXPANSION_RATIO
            )

            var cropLengthHalf = list.maxOrNull() ?: 0f
            val tmp = listOf(centerX, imageWidth - centerX, centerY, imageHeight - centerY)
            cropLengthHalf = min(cropLengthHalf, tmp.maxOrNull() ?: 0f)
            val cropCorner = Pair(centerY - cropLengthHalf, centerX - cropLengthHalf)

            return if (cropLengthHalf > max(imageWidth, imageHeight) / 2f) {
                initRectF(imageWidth, imageHeight)
            } else {
                val cropLength = cropLengthHalf * 2
                RectF(
                    cropCorner.second / imageWidth,
                    cropCorner.first / imageHeight,
                    (cropCorner.second + cropLength) / imageWidth,
                    (cropCorner.first + cropLength) / imageHeight,
                )
            }
        } else {
            return initRectF(imageWidth, imageHeight)
        }
    }

    /**
     * Calculates the maximum distance from each keypoints to the center location.
     * The function returns the maximum distances from the two sets of keypoints:
     * full 17 keypoints and 4 torso keypoints. The returned information will be
     * used to determine the crop size. See determineRectF for more detail.
     */
    private fun determineTorsoAndBodyDistances(
        keyPoints: List<KeyPoint>,
        targetKeyPoints: List<KeyPoint>,
        centerX: Float,
        centerY: Float
    ): TorsoAndBodyDistance {
        val torsoJoints = listOf(
            BodyPart.LEFT_SHOULDER.position,
            BodyPart.RIGHT_SHOULDER.position,
            BodyPart.LEFT_HIP.position,
            BodyPart.RIGHT_HIP.position
        )

        var maxTorsoYRange = 0f
        var maxTorsoXRange = 0f
        torsoJoints.forEach { joint ->
            val distY = abs(centerY - targetKeyPoints[joint].coordinate.y)
            val distX = abs(centerX - targetKeyPoints[joint].coordinate.x)
            if (distY > maxTorsoYRange) maxTorsoYRange = distY
            if (distX > maxTorsoXRange) maxTorsoXRange = distX
        }

        var maxBodyYRange = 0f
        var maxBodyXRange = 0f
        for (joint in keyPoints.indices) {
            if (keyPoints[joint].score < MIN_CROP_KEYPOINT_SCORE) continue
            val distY = abs(centerY - keyPoints[joint].coordinate.y)
            val distX = abs(centerX - keyPoints[joint].coordinate.x)

            if (distY > maxBodyYRange) maxBodyYRange = distY
            if (distX > maxBodyXRange) maxBodyXRange = distX
        }
        return TorsoAndBodyDistance(
            maxTorsoYRange,
            maxTorsoXRange,
            maxBodyYRange,
            maxBodyXRange
        )
    }
    private fun keyPoints_to_skeleton(keyPoints_float : MutableList<KeyPoint>): DoubleArray{
        var skeleton = DoubleArray(36)
        //0: nose
        skeleton[0] = keyPoints_float[0].coordinate.x.toDouble()
        skeleton[1] = keyPoints_float[0].coordinate.y.toDouble()
        //1: left_eye
        skeleton[30] = keyPoints_float[1].coordinate.x.toDouble()
        skeleton[31] = keyPoints_float[1].coordinate.y.toDouble()
        //2: right_eye
        skeleton[28] = keyPoints_float[2].coordinate.x.toDouble()
        skeleton[29] = keyPoints_float[2].coordinate.y.toDouble()
        //3: left_ear
        skeleton[34] = keyPoints_float[3].coordinate.x.toDouble()
        skeleton[35] = keyPoints_float[3].coordinate.y.toDouble()
        //4: right_ear
        skeleton[32] = keyPoints_float[4].coordinate.x.toDouble()
        skeleton[33] = keyPoints_float[4].coordinate.y.toDouble()
        //5: left_shoulder
        skeleton[10] = keyPoints_float[5].coordinate.x.toDouble()
        skeleton[11] = keyPoints_float[5].coordinate.y.toDouble()
        //6: right_shoulder
        skeleton[4] = keyPoints_float[6].coordinate.x.toDouble()
        skeleton[5] = keyPoints_float[6].coordinate.y.toDouble()
        //7: left_elbow
        skeleton[12] = keyPoints_float[7].coordinate.x.toDouble()
        skeleton[13] = keyPoints_float[7].coordinate.y.toDouble()
        //8: right_elbow
        skeleton[6] = keyPoints_float[8].coordinate.x.toDouble()
        skeleton[7] = keyPoints_float[8].coordinate.y.toDouble()
        //9: left_wrist
        skeleton[14] = keyPoints_float[9].coordinate.x.toDouble()
        skeleton[15] = keyPoints_float[9].coordinate.y.toDouble()
        //10: right_wrist
        skeleton[8] = keyPoints_float[10].coordinate.x.toDouble()
        skeleton[9] = keyPoints_float[10].coordinate.y.toDouble()
        //11: left_hip
        skeleton[22] = keyPoints_float[11].coordinate.x.toDouble()
        skeleton[23] = keyPoints_float[11].coordinate.y.toDouble()
        //12: right_hip
        skeleton[16] = keyPoints_float[12].coordinate.x.toDouble()
        skeleton[17] = keyPoints_float[12].coordinate.y.toDouble()
        //13: left_knee
        skeleton[24] = keyPoints_float[13].coordinate.x.toDouble()
        skeleton[25] = keyPoints_float[13].coordinate.y.toDouble()
        //14: right_knee
        skeleton[18] = keyPoints_float[14].coordinate.x.toDouble()
        skeleton[19] = keyPoints_float[14].coordinate.y.toDouble()
        //15: left_ankle
        skeleton[26] = keyPoints_float[15].coordinate.x.toDouble()
        skeleton[27] = keyPoints_float[15].coordinate.y.toDouble()
        //16: right_ankle
        skeleton[20] = keyPoints_float[16].coordinate.x.toDouble()
        skeleton[21] = keyPoints_float[16].coordinate.y.toDouble()
        //17: neck
        /*
        skeleton[2] = 0.0
        skeleton[3] = 0.0
         */
        skeleton[2] = (skeleton[10] + skeleton[4]) / 2
        skeleton[3] = (skeleton[11] + skeleton[5]) / 2

        return skeleton
    }
}
