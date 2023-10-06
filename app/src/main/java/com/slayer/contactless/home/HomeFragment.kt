package com.slayer.contactless.home

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
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
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.slayer.contactless.R
import com.slayer.contactless.captue_activity.CaptureActivityPortrait
import com.slayer.contactless.common.Constants
import com.slayer.contactless.common.Utils
import com.slayer.contactless.databinding.FragmentHomeBinding
import com.slayer.contactless.scan_method_dialog.ScanMethodDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val animatorSet = AnimatorSet()
    private lateinit var scanOptions: ScanOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeScanOptions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCountryCodePicker()

        binding.containerPhone.setEndIconOnClickListener {
            launchMethodDialog()
        }

        observePhoneValidation()
        observeKeyboardVisibility()
        observeClipboard()
        observePhoneTextChanges()

        openTelegram()
        openWhatsapp()

        setupLottieClickListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun launchMethodDialog() {
        val scanMethodDialog = ScanMethodDialog()

        scanMethodDialog.show(childFragmentManager, this.tag)
        scanMethodDialog.setFragmentResultListener(Constants.SCAN_METHOD_REQUEST_KEY) { _, bundle ->
            when (bundle.getString(Constants.SCAN_METHOD_KEY)) {
                Constants.SCAN_METHOD_QR -> {
                    scanQr.launch(scanOptions)
                }

                Constants.SCAN_METHOD_GALLERY -> {
                    pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                }

                Constants.SCAN_METHOD_CAMERA -> {
                    // start fragment and wait result
                    findNavController().navigate(R.id.cameraFragment)
                    setFragmentResultListener(Constants.CAMERA_RESULT_REQUEST_KEY) { _, bundle ->
                        val result = bundle.getString(Constants.CAMERA_RESULT_KEY)
                        if (result != null) {
                            handleScanResult(result)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    private fun initializeScanOptions() {
        scanOptions = ScanOptions()
        scanOptions.setPrompt("")
        scanOptions.setCameraId(0)
        scanOptions.setOrientationLocked(true)
        scanOptions.captureActivity = CaptureActivityPortrait::class.java
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
        val telegramAnimator =
            createButtonAnimator(binding.btnTelegram, value, R.color.telegram_blue)
        val whatsappAnimator =
            createButtonAnimator(binding.btnWhatsapp, value, R.color.whatsapp_green)

        animatorSet.playTogether(telegramAnimator, whatsappAnimator)
        animatorSet.start()
    }

    private fun createButtonAnimator(
        button: MaterialButton,
        enabled: Boolean,
        colorRes: Int
    ): ObjectAnimator {
        val startColor = if (enabled) ContextCompat.getColor(
            requireContext(),
            R.color.greyed_out_color
        ) else ContextCompat.getColor(requireContext(), colorRes)
        val endColor = if (enabled) ContextCompat.getColor(
            requireContext(),
            colorRes
        ) else ContextCompat.getColor(requireContext(), R.color.greyed_out_color)

        val colorAnimator = ObjectAnimator.ofInt(
            button,
            "backgroundColor",
            startColor,
            endColor
        )

        colorAnimator.setEvaluator(ArgbEvaluator())
        colorAnimator.duration = 300 // Adjust the duration as needed

        button.isEnabled = enabled

        return colorAnimator
    }

    private fun openTelegram() {
        binding.btnTelegram.setOnClickListener {
            val number = binding.ccp.fullNumberWithPlus
            val url = "https://t.me/$number"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun openWhatsapp() {
        binding.btnWhatsapp.setOnClickListener {
            val number = binding.ccp.fullNumberWithPlus
            val url = "https://wa.me/$number"

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
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener {
            if (
                clipboardManager.hasPrimaryClip() &&
                (clipboardManager.primaryClip?.itemCount ?: 0) > 0
            ) {
                val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
                if (PhoneNumberUtils.isGlobalPhoneNumber(clipboardText)) {
                    binding.ccp.fullNumber = clipboardText.substring(3, clipboardText.length)
                }
            }
        }
    }

    private fun setupLottieClickListener() {
        binding.apply {
            animationView.setOnClickListener {
                animationView.playAnimation()
            }
        }
    }

    private fun tryReadingText(uri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image: InputImage? = try {
            InputImage.fromFilePath(requireContext(), uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        image?.let {
            // TODO : LOADING DIALOG
            recognizer.process(it).addOnSuccessListener { visionText ->
                handleScanResult(visionText.text)
            }.addOnFailureListener { e ->

            }
        }
    }

    private fun handleScanResult(scanResult: String) {
        val matches = Utils.getPhoneMatches(scanResult)
        when (Utils.handleScanResult(matches)) {
            Constants.SCAN_RESULT_EMPTY -> {
                Toast.makeText(
                    requireContext(),
                    scanResult,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            Constants.SCAN_RESULT_SINGLE -> {
                if (matches.first().contains('+')) {
                    binding.ccp.fullNumber = matches.first()
                } else {
                    binding.containerPhone.editText?.setText(matches.first())
                }
                return
            }

            Constants.SCAN_RESULT_MULTIPLE -> {
                // TODO : // if more than 1 number in matches show a dialog to choose one
                Toast.makeText(requireContext(), "multiple", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val scanQr = registerForActivityResult(ScanContract()) { result ->
        handleScanResult(result.contents)
    }

    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            tryReadingText(uri)
        }
    }
}