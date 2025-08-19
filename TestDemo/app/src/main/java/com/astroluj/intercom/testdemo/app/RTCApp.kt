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

        @JvmField var partnerIP = ""

        @JvmStatic val rxSignalling: RxSignalling by lazy {
            object : RxSignalling() {
                override fun onRxReceive(msg: SignalMessage) {
                    Log.d ("AAAAAAA", "recv : ${msg.ip}:${msg.port} -> ${msg.message}")
                    rtcIntercom.onSignallingReceive(msg.message)
                }

                override fun onRxError(error: Throwable) {
                    error.printStackTrace()
                    Log.d ("AAAAAAA", "??Error")
                    // this.release()
                    rtcIntercom.onError(error)
                }
            }
        }

        @JvmStatic val rtcIntercom: RTCIntercom by lazy {
            object: RTCIntercom(context) {

                override fun onConnected() {
                    Log.d ("AAAAAAAAAA", "connect")
                    //rxSignalling.release()
                }

                override fun onDisconnected() {
                    Log.d ("AAAAAAAAAA", "disconnect")
                    // this.release()
                    // rxSignalling.release()
                }

                override fun onError(e: Throwable?) {
                    Log.d ("AAAAAAAAAA", "Azzzzz ${e?.message?: "??"}")
                    // this.release()
                    // rxSignalling.release()
                }

                override fun onPacketSignalling(jsonStr: String) {
                    Log.d ("AAAAAAA", "send : $jsonStr")
                    rxSignalling.sendPacket(JSONObject(jsonStr), partnerIP, RTCIntercom.DEFAULT_PORT)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}