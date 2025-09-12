package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity

@Dao
interface PathologyTestReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminderPathologyTestData(pathologyTestReminderEntity: PathologyTestReminderEntity): Long

    @Update
    suspend fun updatePathologyTestReminder(pathologyTestReminderEntity: PathologyTestReminderEntity)

    @Query("SELECT * FROM pathology_test")
    suspend fun getAllPathologyTestReminder(): List<PathologyTestReminderEntity>

    @Delete
    suspend fun deletePathologyTestReminder(pathologyTestReminderEntity: PathologyTestReminderEntity)
}