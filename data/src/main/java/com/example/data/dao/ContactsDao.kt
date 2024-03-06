package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contacts_table")
    fun getAllContacts() : Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact : ContactEntity)

    @Delete
    suspend fun deleteContact(contact : ContactEntity)

    @Update
    suspend fun updateContact(contact : ContactEntity)
}