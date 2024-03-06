package com.slayer.contactless.ui.fragments.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Contact
import com.example.domain.repositories.ContactsRepository
import com.slayer.contactless.core.QrScanManager
import com.slayer.contactless.core.TextRecognizerManager
import com.slayer.contactless.common.Constants
import com.slayer.contactless.common.Utils
import com.slayer.contactless.common.result_models.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val qrScanManager: QrScanManager,
    private val textRecognizerManager: TextRecognizerManager,
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    private var _scanResult: MutableStateFlow<ScanResult?> = MutableStateFlow(null)
    val scanResult = _scanResult.asStateFlow()

    fun getScanOptions() = qrScanManager.getScanOptions()

    fun extractPhoneNumbers(scanResult: String) {
        val matches = Utils.extractPhoneNumbers(scanResult)

        val scanResult = when (Utils.handleScanResult(matches)) {
            Constants.SCAN_RESULT_EMPTY -> ScanResult.Empty
            Constants.SCAN_RESULT_SINGLE -> ScanResult.SingleMatch(matches[0])
            Constants.SCAN_RESULT_MULTIPLE -> ScanResult.MultipleMatches(matches)
            else -> ScanResult.Empty
        }

        _scanResult.value = scanResult
    }

    fun readTextFromImageUri(uri: Uri) {
        textRecognizerManager.setImage(uri)
            .onSuccess { text -> extractPhoneNumbers(text) }
            .onFailure { exception -> exception.printStackTrace() }
            .processImage()
    }

    fun insertContact(phone : String) = viewModelScope.launch (Dispatchers.IO) {
        contactsRepository.insertContact(Contact(0,phone,"test"))
    }
}