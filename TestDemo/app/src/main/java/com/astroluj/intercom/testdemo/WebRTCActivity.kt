package com.astroluj.intercom.testdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.astroluj.intercom.NSIntercom
import com.astroluj.intercom_testdemo.R

class WebRTCActivity : AppCompatActivity() {
    val nsIntercom: NSIntercom by lazy {
        object: NSIntercom(this) {
            override fun onPacketSignalling(jsonStr: String, partnerIP: String) {
                nsIntercom.onSignallingReceive(jsonStr)
            }

            override fun onConnected() {
            }

            override fun onError(e: Throwable?) {
                this.release()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        nsIntercom.isSpeakerMode = false
        nsIntercom.partnerIP = intent.getStringExtra("partnerIp")!!
        nsIntercom.start(intent.getBooleanExtra("isInitiator", false), false)
    }

    override fun onDestroy() {
        super.onDestroy()
        nsIntercom.release()
    }
}