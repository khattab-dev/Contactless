package com.slayer.contactless.di

import android.content.Context
import com.slayer.contactless.core.ClipboardManager
import com.slayer.contactless.core.QrScanManager
import com.slayer.contactless.core.TextRecognizerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideQrScanManager() = QrScanManager()

    @Provides
    @Singleton
    fun provideTextRecognizerManager(@ApplicationContext context: Context) = TextRecognizerManager(context)

    @Provides
    @Singleton
    fun provideClipboardManager(@ApplicationContext context: Context) = ClipboardManager(context)
}