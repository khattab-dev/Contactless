package com.slayer.contactless.home

import android.animation.AnimatorSet
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.imageview.ShapeableImageView
import com.journeyapps.barcodescanner.ScanContract
import com.slayer.contactless.R
import com.slayer.contactless.common.Constants
import com.slayer.contactless.common.Utils
import com.slayer.contactless.common.result_models.ScanResult
import com.slayer.contactless.common_ui.AnimationUtils
import com.slayer.contactless.databinding.FragmentHomeBinding
import com.slayer.contactless.scan_method_dialog.ScanMethodDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val animatorSet = AnimatorSet()

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCountryCodePicker()

        setupPhoneContainerEndIconClickedListener()

        observePhoneValidation()
        observeKeyboardVisibility()
        observeClipboard()
        observePhoneTextChanges()
        observeQrResult()

        openTelegram()
        openWhatsapp()

        setupLottieClickListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupPhoneContainerEndIconClickedListener() {
        binding.containerPhone.setEndIconOnClickListener {
            launchMethodDialog()
        }
    }

    private fun observeQrResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scanResult.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                it?.let {
                    handleScanResult(it)
                }
            }
        }
    }

    private fun handleScanResult(it: ScanResult) {
        when (it) {
            is ScanResult.Empty -> {
                Toast.makeText(requireContext(), "empty", Toast.LENGTH_SHORT).show()
            }

            is ScanResult.SingleMatch -> {
                if (it.result.contains('+')) {
                    binding.ccp.fullNumber = it.result
                } else {
                    binding.containerPhone.editText?.setText(it.result)
                }
            }

            is ScanResult.MultipleMatches -> {
                // TODO : // if more than 1 number in matches show a dialog to choose one
                Toast.makeText(requireContext(), "multiple", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchMethodDialog() {
        val scanMethodDialog = ScanMethodDialog()
        scanMethodDialog.show(childFragmentManager, this.tag)

        // Set a listener to handle the result from the dialog
        scanMethodDialog.setFragmentResultListener(Constants.SCAN_METHOD_REQUEST_KEY) { _, bundle ->
            handleScanMethodResult(bundle.getString(Constants.SCAN_METHOD_KEY))
        }
    }

    private fun handleScanMethodResult(scanMethod: String?) {
        when (scanMethod) {
            Constants.SCAN_METHOD_QR -> {
                scanQr.launch(viewModel.getScanOptions())
            }

            Constants.SCAN_METHOD_GALLERY -> {
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }

            Constants.SCAN_METHOD_CAMERA -> {
                startCameraFragment()
            }
        }
    }

    private fun startCameraFragment() {
        findNavController().navigate(R.id.cameraFragment)

        // Set a listener to handle the result from the CameraFragment
        setFragmentResultListener(Constants.CAMERA_RESULT_REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(Constants.CAMERA_RESULT_KEY)
            if (result != null) {
                viewModel.extractPhoneNumbers(result)
            }
        }
    }

    private fun observePhoneTextChanges() {
        binding.containerPhone.editText?.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                setValidationCirclesBackgroundColor(binding.ivEmpty, android.R.color.holo_red_dark)
            } else {
                setValidationCirclesBackgroundColor(
                    binding.ivEmpty,
                    android.R.color.holo_green_dark
                )
            }
        }
    }

    private fun setValidationCirclesBackgroundColor(imageView: ShapeableImageView, colorRes: Int) {
        imageView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                colorRes
            )
        )
    }

    private fun setupCountryCodePicker() {
        binding.ccp.registerCarrierNumberEditText(binding.containerPhone.editText)
    }

    private fun observePhoneValidation() {
        binding.ccp.setPhoneNumberValidityChangeListener {
            startAnimation(it)

            if (it) {
                setValidationCirclesBackgroundColor(
                    binding.ivNotValid,
                    android.R.color.holo_green_dark
                )
            } else {
                setValidationCirclesBackgroundColor(
                    binding.ivNotValid,
                    android.R.color.holo_red_dark
                )
            }
        }
    }

    private fun startAnimation(value: Boolean) {
        val telegramAnimator = AnimationUtils.createButtonAnimator(
            requireContext(),
            binding.btnTelegram,
            value,
            R.color.telegram_blue
        )

        val whatsappAnimator = AnimationUtils.createButtonAnimator(
            requireContext(),
            binding.btnWhatsapp,
            value,
            R.color.whatsapp_green
        )

        animatorSet.playTogether(telegramAnimator, whatsappAnimator)
        animatorSet.start()
    }

    private fun openTelegram() {
        binding.btnTelegram.setOnClickListener {
            val number = binding.ccp.fullNumberWithPlus
            val url = Utils.createWhatsAppUrl(number)

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun openWhatsapp() {
        binding.btnWhatsapp.setOnClickListener {
            val number = binding.ccp.fullNumberWithPlus
            val url = Utils.createTelegramUrl(number)

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun observeKeyboardVisibility() {
        binding.apply {
            root.viewTreeObserver.addOnGlobalLayoutListener {
                val heightDiff = root.rootView.height - root.height
                if (heightDiff > 250) {
                    root.postDelayed({ root.scrollTo(0, root.bottom) }, 0)
                } else {
                    btnTelegram.visibility = View.VISIBLE
                    btnWhatsapp.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeClipboard() {
        viewModel.startClipboardMonitoring {
            viewModel.extractPhoneNumbers(it)
        }
    }

    private fun setupLottieClickListener() {
        binding.animationView.apply {
            setOnClickListener { playAnimation() }
        }
    }

    private val scanQr = registerForActivityResult(ScanContract()) { result ->
        result?.contents?.let { viewModel.extractPhoneNumbers(it) }
    }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let { viewModel.readTextFromImageUri(uri) }
    }
}