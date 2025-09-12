package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whodas_question_table")
data class WhodasQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val questionFirsAnswertId: Int,
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

    val questionEleventhAnswerId: Int,
    val questionEleventhAnswer: String,

    val questionTwelweAnswerId: Int,
    val questionTwelweAnswer: String,

    val totalScore: Float,

    val firstQuestionAnswer: Int,
    val secondQuestionAnswer: Int,
    val thirdQuestionAnswer: Int,

    val date : String,
)
