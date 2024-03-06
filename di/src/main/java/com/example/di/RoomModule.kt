package com.example.di

import android.content.Context
import android.provider.ContactsContract.Contacts
import androidx.room.Room
import com.example.data.ContactsDatabase
import com.example.data.dao.ContactsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ContactsDatabase {
        return Room.databaseBuilder(
            context,
            ContactsDatabase::class.java,
            "contacts_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideContactsDao(
        contactsDatabase: ContactsDatabase
    ): ContactsDao = contactsDatabase.getContactsDao()
}