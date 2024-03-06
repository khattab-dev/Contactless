package com.slayer.contactless.ui.fragments.contacts

import androidx.lifecycle.ViewModel
import com.example.domain.repositories.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {
    val contacts = contactsRepository.getAllContacts()

}