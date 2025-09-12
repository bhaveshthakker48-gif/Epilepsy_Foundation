package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.QolieQuestionEntity

@Dao
interface QolieQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQolieQuestionData(questionEntity: QolieQuestionEntity)

    @Query("SELECT * FROM qolie_question_table")
    suspend fun getAllQolieQuestion(): List<QolieQuestionEntity>

}