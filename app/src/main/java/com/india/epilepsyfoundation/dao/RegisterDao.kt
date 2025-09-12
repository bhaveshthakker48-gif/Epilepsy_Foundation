package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.RegisterEntity

@Dao
interface RegisterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(register: RegisterEntity)

    @Query("SELECT * FROM register_details")
    suspend fun getAll(): List<RegisterEntity>
}
