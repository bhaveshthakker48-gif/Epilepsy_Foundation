package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.ReminderNotificationEntity

@Dao
interface ReminderNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminderNotificationEntity: ReminderNotificationEntity)

    @Query("SELECT * FROM reminder_notification_entity")
    suspend fun getAll(): List<ReminderNotificationEntity>
}
