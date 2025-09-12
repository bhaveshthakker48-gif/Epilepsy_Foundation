package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctor_visit")
data class DoctorVisitReminderEntity (

    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val doctorName : String,
    val doctorVisitDate : String,
    val doctorVisitTime: String,
    val reminder : Boolean = false

)