package com.slayer.contactless.common.result_models

sealed class ScanResult {
    data class SingleMatch(val result: String) : ScanResult()
    data class MultipleMatches(val matches: List<String>) : ScanResult()
    object Empty : ScanResult()
}

