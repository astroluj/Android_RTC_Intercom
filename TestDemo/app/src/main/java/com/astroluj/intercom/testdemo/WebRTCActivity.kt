package com.astroluj.intercom.testdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.astroluj.intercom.rtc.NeoRTC
import com.astroluj.intercom.rtc.RxSignalling
import com.astroluj.intercom_testdemo.R
import org.json.JSONObject

class WebRTCActivity : AppCompatActivity() {
    val rxSignalling: RxSignalling by lazy {
        object : RxSignalling() {
            override fun onRxError(e: Throwable) {
                e.printStackTrace()
                //Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_LONG).show()
                this.release()
                neoRTC.release()
            }

            override fun onRxReceive(json: String) {
                neoRTC.onSignallingReceive(json)
            }

        }
    }
    val neoRTC: NeoRTC by lazy {
        object : NeoRTC(applicationContext) {
            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                rxSignalling.release()
                this.release()
            }

            override fun onPacketSignalling(jsonStr: String, partnerIP: String) {
                rxSignalling.sendPacket(JSONObject(jsonStr), partnerIP)
            }

            override fun onConnected () {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webrtc)

        neoRTC.isSpeakerMode = false
        neoRTC.partnerIP = intent.getStringExtra("partnerIp")!!
        neoRTC.start(intent.getBooleanExtra("isInitiator", false))
        rxSignalling.startSignalling()
    }

    override fun onDestroy() {
        super.onDestroy()
        rxSignalling.release()
        neoRTC.release()
    }
}