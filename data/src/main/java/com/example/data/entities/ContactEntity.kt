package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("contacts_table")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long,
    val phone : String,
    val name : String
)
