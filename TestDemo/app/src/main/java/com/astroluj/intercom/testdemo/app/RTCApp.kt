package com.astroluj.intercom.testdemo.app

import android.app.Application
import android.content.Context
import android.util.Log
import com.astroluj.intercom.RTCIntercom
import com.astroluj.intercom.RxSignalling
import org.json.JSONObject

class RTCApp: Application() {
    companion object {
        lateinit var context: Context

        @JvmStatic val rxSignalling: RxSignalling by lazy {
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

        @JvmStatic val rtcIntercom: RTCIntercom by lazy {
            object: RTCIntercom(context) {
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
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}