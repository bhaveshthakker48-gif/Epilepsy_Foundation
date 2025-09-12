package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stigma_scale_question_table")
data class StigmaScaleQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val questionFirstAnswerId: Int,
    val questionFirstAnswer: String,

    val questionSecondAnswerId: Int,
    val questionSecondAnswer: String,

    val questionThirdAnswerId: Int,
    val questionThirdAnswer: String,

    val questionFourthAnswerId: Int,
    val questionFourthAnswer: String,

    val questionFifthAnswerId: Int,
    val questionFifthAnswer: String,

    val questionSixthAnswerId: Int,
    val questionSixthAnswer: String,

    val questionSeventhAnswerId: Int,
    val questionSeventhAnswer: String,

    val questionEighthAnswerId: Int,
    val questionEighthAnswer: String,

    val questionNinthAnswerId: Int,
    val questionNinthAnswer: String,

    val questionTenthAnswerId: Int,
    val questionTenthAnswer: String,

    val totalScore: Int,

    val date : String,
)
