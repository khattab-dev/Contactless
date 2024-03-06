package com.slayer.contactless.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.slayer.contactless.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check if the device is running less than Android 10
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            val serviceIntent = Intent(this, ClipboardService::class.java)
//            startService(serviceIntent)
//        }
    }
}