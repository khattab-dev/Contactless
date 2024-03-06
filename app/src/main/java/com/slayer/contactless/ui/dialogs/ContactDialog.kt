package com.slayer.contactless.ui.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.domain.models.Contact
import com.slayer.contactless.R
import com.slayer.contactless.common.Constants
import com.slayer.contactless.common.Utils
import com.slayer.contactless.databinding.DialogContactBinding
import com.slayer.contactless.databinding.DialogScanMethodBinding
import com.slayer.contactless.ui.fragments.contacts.ContactsViewModel
import com.slayer.contactless.ui.fragments.home.HomeFragment

class ContactDialog : DialogFragment() {
    private var _binding: DialogContactBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.RoundedCornersDialog

    private val vm: ContactsViewModel by viewModels(
        ownerProducer = { this }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogContactBinding.inflate(inflater, container, false)

        val contact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("contact", Contact::class.java)
        } else {
            arguments?.getParcelable("contact") as Contact?
        }

        binding.tvName.text = contact?.name
        binding.tvPhone.text = contact?.phone
        binding.apply {
            btnTelegram.setOnClickListener {
                openTelegram()
            }

            btnWhatsapp.setOnClickListener {
                openWhatsapp()
            }

            btnDial.setOnClickListener {
                openDialer()
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openTelegram() {
        val number = binding.tvPhone.text.toString()
        val url = Utils.createWhatsAppUrl(number)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun openDialer() {
        val number = binding.tvPhone.text.toString()

        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$number")
        startActivity(dialIntent)
    }

    private fun openWhatsapp() {
        val number = binding.tvPhone.text.toString()
        val url = Utils.createTelegramUrl(number)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}