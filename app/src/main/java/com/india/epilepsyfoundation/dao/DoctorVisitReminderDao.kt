package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity

@Dao
interface DoctorVisitReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminderDoctorVisitData(doctorVisitReminderEntity: DoctorVisitReminderEntity): Long

    @Query("SELECT * FROM doctor_visit")
    suspend fun getAllDoctorVisitReminder(): List<DoctorVisitReminderEntity>

    @Update
    suspend fun updateDoctorVisitReminder(doctorVisitReminderEntity: DoctorVisitReminderEntity)

    @Delete
    suspend fun deleteDoctorVisitReminder(visit: DoctorVisitReminderEntity)
}