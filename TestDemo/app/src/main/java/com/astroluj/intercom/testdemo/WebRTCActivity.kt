package com.astroluj.intercom.testdemo

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.astroluj.intercom.RTCIntercom
import com.astroluj.intercom.RxSignalling
import com.astroluj.intercom.testdemo.R
import com.neosecu.cameramanager.preview.CameraTexture
import com.neosecu.cameramanager.util.CameraUtils
import org.json.JSONObject

class WebRTCActivity : AppCompatActivity() {
    private val frameLayout by lazy { findViewById<FrameLayout>(R.id.cameraFrameLayout) }
    private val remoteLayout by lazy { findViewById<org.webrtc.SurfaceViewRenderer>(R.id.remoteCameraFrameLayout) }
    private var cameraTexture: CameraTexture? = null

    // rtp communication
    private val rxSignalling: RxSignalling by lazy {
        object : RxSignalling() {
            override fun onRxReceive(json: String) {
                Log.d ("AAAAAAA", "recv : $json")
                rtcIntercom.onSignallingReceive(json)
            }

            override fun onRxError(error: Throwable) {
                error.printStackTrace()
                this.release()
                rtcIntercom.onError(error)
            }
        }
    }

    private val rtcIntercom: RTCIntercom by lazy {
        object: RTCIntercom(applicationContext) {
            override fun onConnected(partnerIP: String, partnerPort: Int) {
                Log.d ("AAAAAAAAAA", "connect $partnerIP, $partnerPort")
                rxSignalling.release()
            }

            override fun onDisconnected(partnerIP: String, partnerPort: Int) {
                Log.d ("AAAAAAAAAA", "disconnect $partnerIP, $partnerPort")
            }

            override fun onError(e: Throwable?) {
                this.release()
                rxSignalling.release()
                Log.d ("AAAAAAAAAA", "Azzzzz ${e?.message?: "??"}")
            }

            override fun onPacketSignalling(jsonStr: String, partnerIP: String, partnerPort: Int) {
                Log.d ("AAAAAAA", "send : $jsonStr")
                rxSignalling.sendPacket(JSONObject(jsonStr), partnerIP, partnerPort)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        rtcIntercom.isSpeakerMode = false
        rtcIntercom.streamType = AudioManager.STREAM_VOICE_CALL
        rtcIntercom.partnerIP = intent.getStringExtra("partnerIp")!!
    }

    override fun onResume() {
        super.onResume()
        this.initCamera()
    }

    override fun onPause() {
        super.onPause()
        this.cameraTexture?.releaseCamera()
        rtcIntercom.release()
        rxSignalling.release()
        this.isA = false
    }

    private var isA = false
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
                data: ByteArray, camera: android.hardware.Camera,
                width: Int, height: Int,
                yuvDegree: Int, deviceDegree: Int, isFlashMode: Boolean
            ) {
                if (isA) {
                    rtcIntercom.onUpdateFrame(data, width, height)
                }
                else {
                    isA = !isA
                    rtcIntercom.start(intent.getBooleanExtra("isInitiator", false),
                        isUsedVideo = true, isUsedAudio = false,
                        remoteView = remoteLayout)
                    rxSignalling.startSignalling(rtcIntercom.myPort)
                }
            }

            override fun registerLightSensorFailed() {}
            override fun registerOrientSensorFailed() {}
            override fun onCompleteRecording(path: String?) {}
        }

        frameLayout.removeAllViews()
        frameLayout.addView(cameraTexture, 0)

        this.cameraTexture?.isSecureSurface = false
    }
}