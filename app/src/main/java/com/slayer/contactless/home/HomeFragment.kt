package com.slayer.contactless.home

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.slayer.contactless.R
import com.slayer.contactless.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val animatorSet = AnimatorSet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupCountryCodePicker()

        binding.etPhone.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                binding.ivEmpty.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.holo_red_dark))
            }
            else {
                binding.ivEmpty.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.holo_green_dark))
            }
        }

        observePhoneValidation()
        observeKeyboardVisibility()
        observeClipboard()

        openTelegram()
        openWhatsapp()

        setupLottieClickListener()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupCountryCodePicker() {
        binding.ccp.registerCarrierNumberEditText(binding.etPhone)
    }

    private fun observePhoneValidation() {
        binding.ccp.setPhoneNumberValidityChangeListener {
            startAnimation(it)

            if (it) {
                binding.ivNotValid.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.holo_green_dark))
            }
            else {
                binding.ivNotValid.setBackgroundColor(ContextCompat.getColor(requireContext(),android.R.color.holo_red_dark))
            }
        }
    }

    private fun startAnimation(value : Boolean) {
        val telegramAnimator =
            createButtonAnimator(binding.btnTelegram, value, R.color.telegram_blue)
        val whatsappAnimator =
            createButtonAnimator(binding.btnWhatsapp, value, R.color.whatsapp_green)

        animatorSet.playTogether(telegramAnimator, whatsappAnimator)
        animatorSet.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun openWhatsapp() {
        binding.btnWhatsapp.setOnClickListener {
            val number = binding.ccp.fullNumberWithPlus
            val url = "https://wa.me/$number"

            val intent = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun observeKeyboardVisibility() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = binding.root.rootView.height - binding.root.height
            if (heightDiff > 250) {
                binding.root.postDelayed({
                    binding.root.scrollTo(0, binding.root.bottom)
                }, 0)
            } else {
                binding.btnTelegram.visibility = View.VISIBLE
                binding.btnWhatsapp.visibility = View.VISIBLE
            }
        }
    }

    private fun observeClipboard() {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener {
            if (clipboardManager.hasPrimaryClip() && (clipboardManager.primaryClip?.itemCount ?: 0) > 0
            ) {
                val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
                if (PhoneNumberUtils.isGlobalPhoneNumber(clipboardText)) {
                    val number = clipboardText.substring(3, clipboardText.length)
                    binding.ccp.fullNumber = number
                }
            }
        }
    }

    private fun setupLottieClickListener() {
        binding.animationView.setOnClickListener {
            binding.animationView.playAnimation()
        }
    }
}