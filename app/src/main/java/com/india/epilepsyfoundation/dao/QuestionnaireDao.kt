package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.QuestionnaireEntity

@Dao
interface QuestionnaireDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuestionnaireData(questionnaireEntity: QuestionnaireEntity)

    @Query("SELECT * FROM questionnaire_table")
    suspend fun getAllQuestionnaireData(): List<QuestionnaireEntity>

}