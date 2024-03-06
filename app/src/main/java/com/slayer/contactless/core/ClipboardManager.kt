package com.slayer.contactless.core

import android.content.ClipboardManager
import android.content.Context

class ClipboardManager(context: Context) {
    private val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun startClipboardMonitoring(listener: (String) -> Unit) {
        clipboardManager.addPrimaryClipChangedListener {
            if (clipboardManager.hasPrimaryClip() &&
                (clipboardManager.primaryClip?.itemCount ?: 0) > 0
            ) {
                val clipboardText = clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
                listener(clipboardText)
            }
        }
    }
}