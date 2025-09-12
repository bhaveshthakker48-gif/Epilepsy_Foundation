package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medication_reminder")
data class MedicationReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val medicationName : String,
    val startDate : String,
    val startTime : String,
    val duration : String,
    val durationUnit : String,
    val frequency : String,
    val dose : String
)
