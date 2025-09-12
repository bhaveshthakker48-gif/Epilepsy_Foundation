package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity

@Dao
interface RadiologyTestReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminderRadiologyTestData(radiologyTestReminderEntity: RadiologyTestReminderEntity): Long

    @Update
    suspend fun updateRadiologyTestData(radiologyTestReminderEntity: RadiologyTestReminderEntity)

    @Query("SELECT * FROM radiology_test")
    suspend fun getAllRadiologyTestReminder(): List<RadiologyTestReminderEntity>

    @Delete
    suspend fun deleteRadiologyTestReminder(radiologyTestReminderEntity: RadiologyTestReminderEntity)
}