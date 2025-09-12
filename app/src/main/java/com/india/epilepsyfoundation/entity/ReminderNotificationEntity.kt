package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_notification_entity")
data class ReminderNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notificationTitle: String,
    val notificationContent: String,
)
