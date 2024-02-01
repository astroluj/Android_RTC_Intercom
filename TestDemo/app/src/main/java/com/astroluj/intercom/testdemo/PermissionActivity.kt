package com.astroluj.intercom.testdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.astroluj.intercom.testdemo.R

class PermissionActivity : AppCompatActivity() {

    private val permission by lazy { findViewById<Button>(R.id.permission) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        val camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (camera == PackageManager.PERMISSION_GRANTED && recordAudio == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this, PartnerDetectorActivity::class.java))
        } else {
            permission.setOnClickListener {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                    1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1) {
            val permissionDenied = grantResults.any { it == PackageManager.PERMISSION_DENIED }
            if (permissionDenied) {
                Toast.makeText(this, R.string.strange, Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, PartnerDetectorActivity::class.java))
            }
        } else {
            Toast.makeText(this, R.string.strange, Toast.LENGTH_SHORT).show()
        }
    }
}