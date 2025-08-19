package com.astroluj.intercom.testdemo

import android.media.AudioManager
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.astroluj.intercom.RTCIntercom
import com.neosecu.cameramanager.preview.CameraTexture
import com.neosecu.cameramanager.util.CameraUtils
import com.astroluj.intercom.testdemo.app.RTCApp.Companion.rtcIntercom
import com.astroluj.intercom.testdemo.app.RTCApp.Companion.rxSignalling
import java.nio.ByteBuffer

class RTCActivity: AppCompatActivity() {
    private val frameLayout by lazy { findViewById<FrameLayout>(R.id.cameraFrameLayout) }
    private val remoteLayout by lazy { findViewById<org.webrtc.SurfaceViewRenderer>(R.id.remoteCameraFrameLayout) }
    private var cameraTexture: CameraTexture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        remoteLayout.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_UP -> {
                    // rxSignalling.onRxReceive("{'offer':'sdp':'v=0\r\no=- 5005708793632983399 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=extmap-allow-mixed\r\na=msid-semantic: WMS\r\n'}")
                    rtcIntercom.callCreateOffer()
                }
            }

            true
        }
    }

    override fun onResume() {
        super.onResume()
        this.initCamera()

        rtcIntercom.start(
            isUsedVideo = true,
            isUsedAudio = true,
            remoteView = remoteLayout)
        rxSignalling.startSignalling(RTCIntercom.DEFAULT_PORT)
    }

    override fun onPause() {
        super.onPause()
        this.cameraTexture?.releaseCamera()
        rtcIntercom.stop()
        rxSignalling.stop()
    }

    override fun onStop() {
        super.onStop()
        rtcIntercom.release()
        rxSignalling.release()
    }

    private fun initCamera() {
        // camera view
        this.cameraTexture = object : CameraTexture(
            this,
            CameraUtils.getBackCameraIndex(),
            640,
            480,
            WEIGHT,
            false,
            false,
            90
        ) {
            override fun runFrame(
                data: ByteArray,
                width: Int, height: Int,
                format: Int,
                yuvDegree: Int, deviceDegree: Int, isFlashMode: Boolean
            ) {
                rtcIntercom.onUpdateFrame(data, width, height)
            }

            override fun registerLightSensorFailed() {}
            override fun registerOrientSensorFailed() {}
            override fun onCompleteRecording(path: String?) {}
            override fun runVideoStreaming(
                p0: ByteBuffer?,
                p1: MediaFormat?,
                p2: MediaCodec.BufferInfo?,
                p3: Boolean
            ) {}

            override fun runAudioStreaming(
                p0: ByteBuffer?,
                p1: MediaFormat?,
                p2: MediaCodec.BufferInfo?
            ) {}
        }

        frameLayout.removeAllViews()
        frameLayout.addView(cameraTexture, 0)

        this.cameraTexture?.isSecureSurface = false
    }
}