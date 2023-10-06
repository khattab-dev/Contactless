package com.slayer.contactless.common

object Constants {
    const val TELEGRAM_BASE_URL = "https://t.me/"
    const val WHATSAPP_BASE_URL = "https://wa.me/"

    // Scan results
    const val SCAN_RESULT_EMPTY = 0
    const val SCAN_RESULT_SINGLE = 1
    const val SCAN_RESULT_MULTIPLE = 2

    // Scan methods
    const val SCAN_METHOD_REQUEST_KEY = "SCAN_METHOD"
    const val SCAN_METHOD_KEY = "METHOD"
    const val SCAN_METHOD_QR = "qr"
    const val SCAN_METHOD_GALLERY = "gallery"
    const val SCAN_METHOD_CAMERA = "camera"

    // camera
    const val CAMERA_RESULT_REQUEST_KEY = "CAMERA_RESULT"
    const val CAMERA_RESULT_KEY = "CAMERA_RESULT_KEY"

}