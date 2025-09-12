package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.entity.MedicationReminderEntity

@Dao
interface MedicationReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminderMedicationData(medicationReminderEntity: MedicationReminderEntity) : Long

    @Update
    suspend fun updateReminderMedicationData(medicationReminderEntity: MedicationReminderEntity)

    @Query("SELECT * FROM medication_reminder")
    suspend fun getAllMedicationReminder(): List<MedicationReminderEntity>

    @Delete
    suspend fun deleteMedicationReminder(medicationReminderEntity: MedicationReminderEntity)
}