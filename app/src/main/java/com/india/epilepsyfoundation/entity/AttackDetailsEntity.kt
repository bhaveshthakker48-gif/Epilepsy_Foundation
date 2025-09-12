package com.india.epilepsyfoundation.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attack_details")
data class AttackDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dateOfAttack: String,
    val timeOfAttack: String,
    val duration: String,
    val typeOfAttack: String,
    val detailsOfAttack: String,
)
