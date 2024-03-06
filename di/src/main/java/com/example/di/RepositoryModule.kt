package com.example.di

import com.example.data.repositories.ContactsRepoImpl
import com.example.domain.repositories.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindContactsRepo(contactsRepoImpl: ContactsRepoImpl) : ContactsRepository
}