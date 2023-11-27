package com.slayer.contactless.common_ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

object AnimationUtils {
    fun createButtonAnimator(
        context: Context,
        button: MaterialButton,
        enabled: Boolean,
        colorRes: Int
    ): ObjectAnimator {
        val startColor = if (enabled) ContextCompat.getColor(
            context,
            com.google.android.material.R.color.material_on_surface_disabled
        ) else ContextCompat.getColor(context, colorRes)

        val endColor = if (enabled) ContextCompat.getColor(
            context,
            colorRes
        ) else ContextCompat.getColor(context, com.google.android.material.R.color.material_on_surface_disabled)

        val colorAnimator = ObjectAnimator.ofInt(
            button,
            "backgroundColor",
            startColor,
            endColor
        )

        colorAnimator.setEvaluator(ArgbEvaluator())
        colorAnimator.duration = 300 // Adjust the duration as needed

        button.isEnabled = enabled

        return colorAnimator
    }
}