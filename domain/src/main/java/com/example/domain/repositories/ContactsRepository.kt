package com.example.domain.repositories

import com.example.domain.models.Contact
import kotlinx.coroutines.flow.Flow

interface ContactsRepository {
    fun getAllContacts() : Flow<List<Contact>>

    suspend fun insertContact(contact: Contact)
}