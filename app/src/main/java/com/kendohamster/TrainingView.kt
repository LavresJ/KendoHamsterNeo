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

package com.kendohamster

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.kendohamster.data.Device
import com.kendohamster.ml.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.kendohamster.camera.CameraSource as Camera_back
import com.kendohamster.camera.CameraSourceReverse as Camera_front

var motionName_public: String? = null
var classes_probability: ArrayList<Float> = arrayListOf()
var AnkleStep = true
var stepCount = 0.0
var lastAnkleStep = false
var wristAboveShoulder = true
var lastBoolean = false
var start_motion = false
var single_dynamic_motion_frames = 0.0
var single_dynamic_motion_accuracy_sum = 0.0
var total_dynamic_motion_accuracy = 0.0
var static_motion_detect = false
var frontCount = 0.0
var abdominalCount = 0.0
var hold_sword = false
var hold_sword_count = 0.0
var is_dynamic_motion = false
var dynamic_motion_complete = false //判斷動態動作有沒有完成一個週期
var dynamic_motion_judgement = true //判斷該週期的動態動作是正確or錯誤
var dynamic_motion_times_count = 0.0
var normal_end = true
var camera_back = false  //是否是後鏡頭
var timestamp_str: String? = null
var accuracyList: ArrayList<Float> = arrayListOf()

class TrainingView : AppCompatActivity() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    /** A [SurfaceView] for camera preview.   */
    private lateinit var surfaceView: SurfaceView

    /** Default pose estimation model is 1 (MoveNet Thunder)
     * 0 == MoveNet Lightning model
     * 1 == MoveNet Thunder model
     * 2 == MoveNet MultiPose model
     * 3 == PoseNet model
     **/

    ///handler test
    private var countHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var countRunnable: Runnable
    ///

    private var modelPos = 1

    /** Default device is GPU */
    private var device = Device.GPU

    private lateinit var tvKeypoint: TextView

    private var motionName: String? = null
    private var practiceTime = 0

    private lateinit var tvMotionName: TextView
    private lateinit var tvPracticeCount: TextView
    private lateinit var btnStopPractice: Button
    private lateinit var viewReverseCamera: ImageView

    private lateinit var tvScore: TextView
    private lateinit var tvFPS: TextView
    private lateinit var spnDevice: Spinner
    private lateinit var spnModel: Spinner
    private lateinit var spnTracker: Spinner
    private lateinit var vTrackerOption: View
    private lateinit var tvClassificationValue1: TextView
    private lateinit var tvClassificationValue2: TextView
    private lateinit var tvClassificationValue3: TextView
    private lateinit var tvClassificationValue4: TextView
    private lateinit var swClassification: SwitchCompat
    private lateinit var vClassificationOption: View
    private var cameraSource_back: Camera_back? = null
    private var cameraSource_front: Camera_front? = null

    //菜單相關
    var menu_motion_arraylist: ArrayList<String?>? = null
    var from_menu: Boolean? = null

    private var isClassifyPose = false
    private lateinit var falseView: ImageView//動作錯誤圖示
    private lateinit var trueView:ImageView//動作正確圖示
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                com.kendohamster.TrainingView.ErrorDialog.Companion.newInstance(
                    getString(com.kendohamster.R.string.tfe_pe_request_permission)
                )
                    .show(supportFragmentManager,
                        com.kendohamster.TrainingView.Companion.FRAGMENT_DIALOG
                    )
            }
        }
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var changeTrackerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeTracker(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }
    }

    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.kendohamster.R.layout.activity_training_view)
        // keep screen on while app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        //接收intent資料
        val i = intent
        motionName = i.getStringExtra("motionName")
        practiceTime = i.getIntExtra("practiceTime", 0)
        camera_back = i.getBooleanExtra("camera_back", false)
        timestamp_str = i.getStringExtra("time_start")
        //Log.d("time_start", timestamp_str.toString())

        //Log.d("time_start", timestamp_str);
        menu_motion_arraylist = i.getStringArrayListExtra("menu_motion_arraylist")
        from_menu = i.getBooleanExtra("from_menu", false)

        motionName_public = motionName.toString()
        if(motionName.equals("正面劈刀") || motionName.equals("擦足") || motionName.equals("右胴劈刀")
            || motionName.equals("Men Uchi") || motionName.equals("Suri Ashi") || motionName.equals("Dou Uchi")){
            is_dynamic_motion = true
        }else{
            is_dynamic_motion = false
        }
        //Log.d("motionName_public", motionName_public.toString())
        /////
        wristAboveShoulder = true
        lastBoolean = false
        stepCount = 0.0
        frontCount = 0.0
        abdominalCount = 0.0
        single_dynamic_motion_frames = 0.0
        single_dynamic_motion_accuracy_sum = 0.0
        total_dynamic_motion_accuracy = 0.0
        hold_sword_count = 0.0
        hold_sword = false
        dynamic_motion_complete = false
        dynamic_motion_judgement = false
        dynamic_motion_times_count = 0.0
        static_motion_detect = false
        normal_end = true

        accuracyList.clear()

        falseView = findViewById(R.id.falseView)
        trueView = findViewById(R.id.trueView)
        falseView.visibility = View.GONE
        trueView.visibility = View.GONE

        tvKeypoint = findViewById(com.kendohamster.R.id.tvKeypoint)

        tvMotionName = findViewById(R.id.tv_motion_name)
        btnStopPractice = findViewById(R.id.btn_stop_practice)
        tvPracticeCount = findViewById(R.id.tvPracticeCount)
        viewReverseCamera = findViewById(R.id.reverseCameraView)

        tvScore = findViewById(com.kendohamster.R.id.tvScore)
        tvFPS = findViewById(com.kendohamster.R.id.tvFps)
        spnModel = findViewById(com.kendohamster.R.id.spnModel)
        spnDevice = findViewById(com.kendohamster.R.id.spnDevice)
        spnTracker = findViewById(com.kendohamster.R.id.spnTracker)
        vTrackerOption = findViewById(com.kendohamster.R.id.vTrackerOption)
        surfaceView = findViewById(com.kendohamster.R.id.surfaceView)
        tvClassificationValue1 = findViewById(com.kendohamster.R.id.tvClassificationValue1)
        tvClassificationValue2 = findViewById(com.kendohamster.R.id.tvClassificationValue2)
        tvClassificationValue3 = findViewById(com.kendohamster.R.id.tvClassificationValue3)
        tvClassificationValue4 = findViewById(com.kendohamster.R.id.tvClassificationValue4)
        swClassification = findViewById(com.kendohamster.R.id.swPoseClassification)
        vClassificationOption = findViewById(com.kendohamster.R.id.vClassificationOption)
        initSpinner()
        spnModel.setSelection(modelPos)
        swClassification.setOnCheckedChangeListener(setClassificationListener)
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }

        tvMotionName.text = motionName

        btnStopPractice.setOnClickListener(View.OnClickListener {
            normal_end = false

            menu_motion_arraylist?.removeAt(0)

            val i = Intent(this, TrainingResult::class.java)
            i.putExtra("motionName", motionName)
            i.putExtra("practiceTime", practiceTime)
            i.putExtra("accuracyList", accuracyList.toFloatArray())
            i.putExtra("normal_end", normal_end)
            i.putExtra("frontCount", frontCount)
            i.putExtra("stepCount", stepCount)
            i.putExtra("hold_sword_count", hold_sword_count)
            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
            i.putExtra("from_menu", from_menu)

            finish()
            startActivity(i)
        })

        viewReverseCamera.setOnClickListener(View.OnClickListener {
            /*
            if(camera_back) {
                cameraSource_back?.close()
                cameraSource_back = null
            }else{
                cameraSource_front?.close()
                cameraSource_front = null
            }
             */

            cameraSource_back?.close()
            cameraSource_back = null
            cameraSource_front?.close()
            cameraSource_front = null

            if(camera_back){
                camera_back = false
            }else{
                camera_back = true
            }

            val i = Intent(this, TrainingView::class.java)
            i.putExtra("motionName", motionName)
            i.putExtra("practiceTime", practiceTime)
            i.putExtra("camera_back", camera_back)
            i.putExtra("time_start", timestamp_str)
            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
            i.putExtra("from_menu", from_menu)
            this.finish()   //MotionVideo.this.finish();
            startActivity(i)
        })
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun onResume() {
        if(camera_back) {
            cameraSource_back?.resume()
        }else{
            cameraSource_front?.resume()
        }
        super.onResume()

        ///handler test
        countRunnable = Runnable(){
            @Override
            fun run(){
                if(is_dynamic_motion) {
                    if (dynamic_motion_complete) {
                        if (dynamic_motion_judgement) {
                            trueView.visibility = View.VISIBLE     //顯示動作正確圖示
                            falseView.visibility = View.GONE    //不顯示錯誤圖示
                        } else {
                            trueView.visibility = View.GONE //不顯示動作正確圖示
                            falseView.visibility = View.VISIBLE //顯示錯誤圖示
                        }
                        dynamic_motion_times_count = 0.0
                        dynamic_motion_complete = false
                    }

                    if(dynamic_motion_times_count > 0.8){ //顯示0.8秒的時間
                        trueView.visibility = View.GONE
                        falseView.visibility = View.GONE
                    }
                    dynamic_motion_times_count += 0.1
                }
                when (motionName){
                    "正面劈刀","Men Uchi" -> {
                        if ((practiceTime - Math.floor(frontCount).toInt()) <= 0){

                            menu_motion_arraylist?.removeAt(0)

                            val i = Intent(this, TrainingResult::class.java)
                            i.putExtra("motionName", motionName)
                            i.putExtra("practiceTime", practiceTime)
                            i.putExtra("accuracyList", accuracyList.toFloatArray())
                            i.putExtra("frontCount", frontCount)
                            i.putExtra("normal_end", normal_end)
                            i.putExtra("time_start", timestamp_str)
                            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
                            i.putExtra("from_menu", from_menu)
                            showToast("完成訓練")
                            startActivity(i)
                            finish()
                        }
                    tvPracticeCount.text = "" + (practiceTime - Math.floor(frontCount).toInt()) + "次"
                        countHandler.postDelayed(countRunnable, 100)
                    }

                    "擦足","Suri Ashi" -> {
                        if ((practiceTime - Math.floor(stepCount).toInt()) <= 0){

                            menu_motion_arraylist?.removeAt(0)

                            val i = Intent(this, TrainingResult::class.java)
                            i.putExtra("motionName", motionName)
                            i.putExtra("practiceTime", practiceTime)
                            i.putExtra("accuracyList", accuracyList.toFloatArray())
                            i.putExtra("stepCount", stepCount)
                            i.putExtra("normal_end", normal_end)
                            i.putExtra("time_start", timestamp_str)
                            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
                            i.putExtra("from_menu", from_menu)
                            showToast("完成訓練")
                            startActivity(i)
                            finish()
                        }
                        tvPracticeCount.text = "" + (practiceTime - Math.floor(stepCount).toInt()) + "次"
                        countHandler.postDelayed(countRunnable, 100)
                    }

                    "托刀","Waki Kiamae" -> {
                        //這個部分每0.1(0.2)秒會執行一次
                        //使用者需要連續十次被偵測到動作正確，倒數的秒數才會-1

                        if(practiceTime - hold_sword_count <= 0){

                            menu_motion_arraylist?.removeAt(0)

                            val i = Intent(this, TrainingResult::class.java)
                            i.putExtra("motionName", motionName)
                            i.putExtra("practiceTime", practiceTime)
                            i.putExtra("accuracyList", accuracyList.toFloatArray())
                            i.putExtra("hold_sword_count", hold_sword_count)
                            i.putExtra("normal_end", normal_end)
                            i.putExtra("time_start", timestamp_str)
                            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
                            i.putExtra("from_menu", from_menu)
                            showToast("完成訓練")
                            startActivity(i)
                            finish()
                        }

                        //val person = cameraSource?.getPersons()?.get(0)
                        //val keypoints = person?.keyPoints

                        //此處判斷動作是否正確
                        //若動作正確則hold_sword_count+=0.1
                        //動作不正確則hold_sword_count無條件捨棄小數點
                        if(static_motion_detect) {
                            if (hold_sword) { //動作正確 hold_sword
                                hold_sword_count += 0.1
                                trueView.visibility = View.VISIBLE     //顯示動作正確圖示
                                falseView.visibility = View.GONE    //不顯示錯誤圖示
                            } else {
                                hold_sword_count = Math.floor(hold_sword_count)
                                trueView.visibility = View.GONE
                                falseView.visibility = View.VISIBLE
                            }
                        }else{
                            trueView.visibility = View.GONE
                            falseView.visibility = View.GONE
                        }

                        tvPracticeCount.text = "" + (practiceTime - Math.floor(hold_sword_count).toInt()) + "秒"
                        countHandler.postDelayed(countRunnable, 100)
                    }

                    "右胴劈刀","Dou Uchi" -> {
                        if ((practiceTime - Math.floor(abdominalCount).toInt()) <= 0) {

                            menu_motion_arraylist?.removeAt(0)

                            val i = Intent(this, TrainingResult::class.java)
                            i.putExtra("motionName", motionName)
                            i.putExtra("practiceTime", practiceTime)
                            i.putExtra("accuracyList", accuracyList.toFloatArray())
                            i.putExtra("abdominalCount", abdominalCount)
                            i.putExtra("normal_end", normal_end)
                            i.putExtra("time_start", timestamp_str)
                            i.putExtra("menu_motion_arraylist", menu_motion_arraylist)
                            i.putExtra("from_menu", from_menu)
                            showToast("完成訓練")
                            startActivity(i)
                            finish()
                        }
                        tvPracticeCount.text = "" + (practiceTime - Math.floor(abdominalCount).toInt()) + "次"
                        countHandler.postDelayed(countRunnable, 100)
                    }
                }


            }
            run()
        }

        countHandler.postDelayed(countRunnable, 100)
        ///
    }

    override fun onPause() {
        /*
        if(camera_back) {
            cameraSource_back?.close()
            cameraSource_back = null
        }else{
            cameraSource_front?.close()
            cameraSource_front = null
        }
         */
        cameraSource_back?.close()
        cameraSource_back = null
        cameraSource_front?.close()
        cameraSource_front = null

        countHandler.removeCallbacks(countRunnable)

        super.onPause()
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    // open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource_back == null && camera_back) {
                cameraSource_back =
                    Camera_back(surfaceView, object : Camera_back.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(com.kendohamster.R.string.tfe_pe_tv_fps, fps)
                        }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            tvScore.text = getString(com.kendohamster.R.string.tfe_pe_tv_score, personScore ?: 0f)
                            poseLabels?.sortedByDescending { it.second }?.let {
                                tvClassificationValue1.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                )
                                tvClassificationValue2.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 2) it[1] else null)
                                )
                                tvClassificationValue3.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 3) it[2] else null)
                                )
                                tvClassificationValue4.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 4) it[3] else null)
                                )
                            }
                        }


                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource_back?.initCamera()
                }
            }
            if (cameraSource_front == null && !camera_back) {
                cameraSource_front =
                    Camera_front(surfaceView, object : Camera_front.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {
                            tvFPS.text = getString(com.kendohamster.R.string.tfe_pe_tv_fps, fps)
                        }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
                            tvScore.text = getString(com.kendohamster.R.string.tfe_pe_tv_score, personScore ?: 0f)
                            poseLabels?.sortedByDescending { it.second }?.let {
                                tvClassificationValue1.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
                                )
                                tvClassificationValue2.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 2) it[1] else null)
                                )
                                tvClassificationValue3.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 3) it[2] else null)
                                )
                                tvClassificationValue4.text = getString(
                                    com.kendohamster.R.string.tfe_pe_tv_classification_value,
                                    convertPoseLabels(if (it.size >= 4) it[3] else null)
                                )
                            }
                        }


                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource_front?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.3f", pair.second)})"
    }

    private fun isPoseClassifier() {
        if(camera_back) {
            cameraSource_back?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
        }else{
            cameraSource_front?.setClassifier(if (isClassifyPose) PoseClassifier.create(this) else null)
        }
    }

    // Initialize spinners to let user select model/accelerator/tracker.
    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            this,
            com.kendohamster.R.array.tfe_pe_models_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spnModel.adapter = adapter
            spnModel.onItemSelectedListener = changeModelListener
        }

        ArrayAdapter.createFromResource(
            this,
            com.kendohamster.R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnDevice.adapter = adaper
            spnDevice.onItemSelectedListener = changeDeviceListener
        }

        ArrayAdapter.createFromResource(
            this,
            com.kendohamster.R.array.tfe_pe_tracker_array, android.R.layout.simple_spinner_item
        ).also { adaper ->
            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnTracker.adapter = adaper
            spnTracker.onItemSelectedListener = changeTrackerListener
        }
    }

    // Change model when app is running
    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    // Change device (accelerator) type when app is running
    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.GPU
            1 -> Device.CPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    // Change tracker for Movenet MultiPose model
    private fun changeTracker(position: Int) {
        if(camera_back) {
            cameraSource_back?.setTracker(
                when (position) {
                    1 -> TrackerType.BOUNDING_BOX
                    2 -> TrackerType.KEYPOINTS
                    else -> TrackerType.OFF
                }
            )
        }else{
            cameraSource_front?.setTracker(
                when (position) {
                    1 -> TrackerType.BOUNDING_BOX
                    2 -> TrackerType.KEYPOINTS
                    else -> TrackerType.OFF
                }
            )
        }
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        if (!Python.isStarted()) {
            //Python.start(AndroidPlatform(this@TrainingView))
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val pyobj = py.getModule("src/try")


        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(this, device, ModelType.Lightning, pyobj)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(this, device, ModelType.Thunder, pyobj)
            }
            2 -> {
                // MoveNet (Lightning) MultiPose
                showPoseClassifier(false)
                showDetectionScore(false)
                // Movenet MultiPose Dynamic does not support GPUDelegate
                if (device == Device.GPU) {
                    showToast(getString(com.kendohamster.R.string.tfe_pe_gpu_error))
                }
                showTracker(true)
                MoveNetMultiPose.create(this, device, Type.Dynamic)
            }
            3 -> {
                // PoseNet (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                PoseNet.create(this, device)
            }
            else -> {
                null
            }
        }
        if(camera_back) {
            poseDetector?.let { detector ->
                cameraSource_back?.setDetector(detector) //重置detector
            }
        }else{
            poseDetector?.let { detector ->
                cameraSource_front?.setDetector(detector) //重置detector
            }
        }
    }

    // Show/hide the pose classification option.
    private fun showPoseClassifier(isVisible: Boolean) {
        vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (!isVisible) {
            swClassification.isChecked = false
        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {
        tvScore.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    // Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        tvClassificationValue1.visibility = visibility
        tvClassificationValue2.visibility = visibility
        tvClassificationValue3.visibility = visibility
        tvClassificationValue4.visibility = visibility
    }

    // Show/hide the tracking options.
    private fun showTracker(isVisible: Boolean) {
        if (isVisible) {
            // Show tracker options and enable Bounding Box tracker.
            vTrackerOption.visibility = View.VISIBLE
            spnTracker.setSelection(1)
        } else {
            // Set tracker type to off and hide tracker option.
            vTrackerOption.visibility = View.GONE
            spnTracker.setSelection(0)
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                openCamera()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(com.kendohamster.TrainingView.ErrorDialog.Companion.ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): com.kendohamster.TrainingView.ErrorDialog = com.kendohamster.TrainingView.ErrorDialog()
                .apply {
                arguments = Bundle().apply { putString(com.kendohamster.TrainingView.ErrorDialog.Companion.ARG_MESSAGE, message) }
            }
        }
    }
}
