package com.slayer.contactless.common

object Utils {
    fun isPhoneNumber(text: String): Boolean {
        val phoneNumberPattern = Regex("\\+?[0-9]+[\\s-]?\\(?[0-9]+\\)?[\\s-]?[0-9]+[\\s-]?[0-9]+")
        return phoneNumberPattern.matches(text)
    }
    fun extractPhoneNumbers(text: String): List<String> {
        val phoneNumberPattern = Regex("\\+?[0-9]+[\\s-]?\\(?[0-9]+\\)?[\\s-]?[0-9]+[\\s-]?[0-9]+")

        return phoneNumberPattern.findAll(text)
            .map { match ->
                val phoneNumber = match.value.replace(Regex("[^0-9+]"), "")
                phoneNumber
            }
            .toList()
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

    fun createTelegramUrl(number : String) : String {
        return "https://t.me/${number}"
    }

    fun createWhatsAppUrl(number : String) : String {
        return "https://wa.me/${number}"
    }
}