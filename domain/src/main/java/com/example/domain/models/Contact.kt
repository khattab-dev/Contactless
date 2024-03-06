package com.example.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact (
    val id : Long,
    val phone : String,
    val name : String
) : Parcelable