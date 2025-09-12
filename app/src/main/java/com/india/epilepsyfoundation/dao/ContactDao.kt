package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.ContactEntity

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactEntity: ContactEntity)

    @Query("SELECT * FROM contact_details")
    suspend fun getAll(): List<ContactEntity>
}
