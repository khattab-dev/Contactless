package com.example.data.repositories

import com.example.data.dao.ContactsDao
import com.example.data.entities.ContactEntity
import com.example.domain.models.Contact
import com.example.domain.repositories.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactsRepoImpl @Inject constructor(
    private val dao: ContactsDao
) : ContactsRepository {
    override fun getAllContacts(): Flow<List<Contact>> {
        return dao.getAllContacts().map {
            it.map { contact ->
                Contact(
                    id = contact.id,
                    phone = contact.phone,
                    name = contact.name
                )
            }
        }
    }

    override suspend fun insertContact(contact: Contact) {
        dao.insertContact(
            ContactEntity(id = 0, phone = contact.phone, name = "test")
        )
    }
}