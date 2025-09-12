package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pathology_test")
data class PathologyTestReminderEntity(

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val testName : String,
    val visitDate : String,
    val visitTime : String,
    val labName : String,
    val reminderDate : String,
    val reminderTime: String

)
