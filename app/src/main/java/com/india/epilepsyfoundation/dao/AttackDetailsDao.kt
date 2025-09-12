package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.AttackDetailsEntity

@Dao
interface AttackDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAttackDetailsData(attackDetailsEntity: AttackDetailsEntity)

    @Query("SELECT * FROM attack_details")
    suspend fun getAllAttackDetailsData(): List<AttackDetailsEntity>
}
