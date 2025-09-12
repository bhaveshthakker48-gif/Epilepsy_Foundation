package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "register_details")
data class RegisterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val mobileNumber: String,
    val email: String
)
