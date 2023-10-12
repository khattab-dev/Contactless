package com.slayer.contactless

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class TextRecognizerManager(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var image: InputImage? = null
    private var onSuccess: ((String) -> Unit)? = null
    private var onFailure: ((Exception) -> Unit)? = null

    fun setImage(uri: Uri): TextRecognizerManager {
        image = try {
            InputImage.fromFilePath(context, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        return this
    }

    fun onSuccess(onSuccess: (String) -> Unit): TextRecognizerManager {
        this.onSuccess = onSuccess
        return this
    }

    fun onFailure(onFailure: (Exception) -> Unit): TextRecognizerManager {
        this.onFailure = onFailure
        return this
    }

    fun processImage() {
        image?.let { image ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onSuccess?.invoke(visionText.text)
                }
                .addOnFailureListener { exception ->
                    onFailure?.invoke(exception)
                }
        }
    }
}
