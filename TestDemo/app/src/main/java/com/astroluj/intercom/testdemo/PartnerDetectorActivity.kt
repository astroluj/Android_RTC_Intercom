package com.astroluj.intercom.testdemo

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.astroluj.intercom.testdemo.app.RTCApp
import java.util.*

class PartnerDetectorActivity : AppCompatActivity() {

    private val partnerIp by lazy { findViewById<EditText>(R.id.partnerIpEdit) }
    private val connectBtn by lazy { findViewById<Button>(R.id.connectBtn) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signalling)

        connectBtn.setOnClickListener {
            done()
        }
    }

    // Result of permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            15 -> {
                var success = 0
                for (isGranted in grantResults) {
                    if (isGranted == PackageManager.PERMISSION_GRANTED) success++
                }
                // if (success == permissions.size) {}
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 권한이 필요하면 권한 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            val permissionArray = ArrayList<String>()

            if (!isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                //if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO));
                permissionArray.add(Manifest.permission.RECORD_AUDIO)
            }
            if (!isPermissionGranted(Manifest.permission.CAMERA)) {
                //if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO));
                permissionArray.add(Manifest.permission.CAMERA)
            }
            val permissions = arrayOfNulls<String>(permissionArray.size)
            if (permissions.isNotEmpty()) {
                requestPermissions(
                    permissionArray.toArray(permissions),
                    15
                )
            }
        }

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val devices = audioManager.availableCommunicationDevices
            val targetType = if (true) AudioDeviceInfo.TYPE_BUILTIN_SPEAKER else AudioDeviceInfo.TYPE_BUILTIN_EARPIECE

            for (device in devices) {
                if (device.type == targetType) {
                    audioManager.setCommunicationDevice(device)
                    break
                }
            }
        } else {
            audioManager.isSpeakerphoneOn = true
        }

        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        Log.d ("AAAAAAAA", "current volume $currentVolume, $maxVolume")
    }

    // want to Permission granted state
    @TargetApi(Build.VERSION_CODES.M)
    fun isPermissionGranted(permission: String?): Boolean {
        return checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }

    private fun done() {
        if (partnerIp.text.toString().isNotEmpty()) {
            val intent = Intent(this, RTCActivity::class.java)
            intent.putExtra("partnerIp", partnerIp.text.toString())
            RTCApp.partnerIP = partnerIp.text.toString()
            startActivity(intent)
            finish()
        }
    }
}