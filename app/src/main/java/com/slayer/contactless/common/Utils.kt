package com.slayer.contactless.common

object Utils {
    fun isPhoneNumber(text: String): Boolean {
        val phoneNumberPattern = Regex("[+]?[0-9]{10,13}")
        return phoneNumberPattern.matches(text)
    }
    fun getPhoneMatches(text: String): List<String> {
        val phoneNumberPattern = Regex("[+]?[0-9]{10,13}")
        return phoneNumberPattern.findAll(text).map { it.value }.toList()
    }

    fun handleScanResult(matches : List<String>) : Int {
        return if (matches.isEmpty()) {
            Constants.SCAN_RESULT_EMPTY
        } else if (matches.size == 1) {
            Constants.SCAN_RESULT_SINGLE
        } else {
            Constants.SCAN_RESULT_MULTIPLE
        }
    }
}