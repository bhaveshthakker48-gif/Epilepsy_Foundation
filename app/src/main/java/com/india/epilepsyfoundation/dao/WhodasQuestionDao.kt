package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.entity.WhodasQuestionEntity

@Dao
interface WhodasQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWhodasQuestionData(whodasQuestionEntity: WhodasQuestionEntity)

    @Query("SELECT * FROM whodas_question_table")
    suspend fun getAllWhodasQuestion(): List<WhodasQuestionEntity>

}