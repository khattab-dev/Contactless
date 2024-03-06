package com.slayer.contactless.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.slayer.contactless.R
import com.slayer.contactless.common.Constants
import com.slayer.contactless.databinding.DialogScanMethodBinding


class ScanMethodDialog : DialogFragment() {
    private var _binding: DialogScanMethodBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogScanMethodBinding.inflate(inflater, container, false)

        binding.btnQr.setOnClickListener {
            setFragmentResult(Constants.SCAN_METHOD_REQUEST_KEY, Bundle().apply {
                putString(Constants.SCAN_METHOD_KEY, Constants.SCAN_METHOD_QR)
            })
            dismiss()
        }

        binding.btnGallery.setOnClickListener {
            setFragmentResult(Constants.SCAN_METHOD_REQUEST_KEY, Bundle().apply {
                putString(Constants.SCAN_METHOD_KEY, Constants.SCAN_METHOD_GALLERY)
            })
            dismiss()
        }

        binding.btnCamera.setOnClickListener {
            setFragmentResult(Constants.SCAN_METHOD_REQUEST_KEY, Bundle().apply {
                putString(Constants.SCAN_METHOD_KEY, Constants.SCAN_METHOD_CAMERA)
            })
            dismiss()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}