package com.android.hhn.kotlintoy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.hhn.kotlintoy.net.TestNetRequestActivity
import com.android.hhn.kotlintoy.sticker.TestStickerActivity

val isDarkMode: Boolean
    get() = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 0
    }

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kt_activity_main)
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        findViewById<View>(R.id.ac_main_test_net).setOnClickListener(this)
        findViewById<View>(R.id.ac_main_test_sticker).setOnClickListener(this)
        findViewById<View>(R.id.ac_main_test_panorama).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ac_main_test_net -> {
                val intent = Intent(this, TestNetRequestActivity::class.java)
                startActivity(intent)
            }
            R.id.ac_main_test_sticker -> {
                val intent = Intent(this, TestStickerActivity::class.java)
                startActivity(intent)
            }
            R.id.ac_main_test_panorama -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val permissionsAllGranted = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }

            if (permissionsAllGranted) {
                Toast.makeText(this, "permissionsAllGranted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}