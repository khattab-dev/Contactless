package com.slayer.contactless.core

import com.journeyapps.barcodescanner.ScanOptions
import com.slayer.contactless.ui.activities.CaptureActivityPortrait

class QrScanManager {
    private val scanOptions = ScanOptions()

    init {
        scanOptions.setPrompt("")
        scanOptions.setCameraId(0)
        scanOptions.setOrientationLocked(true)
        scanOptions.captureActivity = CaptureActivityPortrait::class.java
    }

    fun getScanOptions() = scanOptions
}