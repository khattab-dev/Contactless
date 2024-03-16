package com.slayer.contactless.ui.fragments.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.domain.models.Contact
import com.slayer.contactless.databinding.FragmentContactsBinding
import com.slayer.contactless.ui.dialogs.ContactDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val vm: ContactsViewModel by viewModels()

    private val adapter = ContactsAdapter {
        onMethodBtnClicked(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        binding.rvContacts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            vm.contacts.collectLatest {
                if (it.isEmpty()) {
                    binding.animationView.visibility = View.VISIBLE
                } else {
                    binding.animationView.visibility = View.GONE
                    adapter.submitList(it)
                }
            }
        }

        return binding.root
    }

    private fun onMethodBtnClicked(contact: Contact) {
        val dialog = ContactDialog()
        val args = Bundle().apply {
            putParcelable("contact", contact)
        }
        dialog.arguments = args
        dialog.show(childFragmentManager, this.tag)
    }
}