package com.india.epilepsyfoundation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity

@Dao
interface StigmaScaleQuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStigmaScaleQuestionData(stigmaScaleQuestionEntity: StigmaScaleQuestionEntity)

    @Query("SELECT * FROM stigma_scale_question_table")
    suspend fun getAllStigmaScaleQuestion(): List<StigmaScaleQuestionEntity>

}