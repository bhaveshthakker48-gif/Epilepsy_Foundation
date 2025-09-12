package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qolie_question_table")
data class QolieQuestionEntity(
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

    val questionEleventhAnswerId: Int,
    val questionEleventhAnswer: String,

    val totalScore: Int,

    val firstPointRank: Int,
    val secondPointRank: Int,
    val thirdPointRank: Int,
    val fourthPointRank: Int,
    val fifthPointRank: Int,
    val sixthPointRank: Int,
    val seventhPointRank: Int,

    val date : String,
)
