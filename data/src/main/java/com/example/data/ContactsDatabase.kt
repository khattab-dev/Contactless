package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.ContactsDao
import com.example.data.entities.ContactEntity

@Database(entities = [ContactEntity::class], version = 1, exportSchema = false)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun getContactsDao() : ContactsDao
}