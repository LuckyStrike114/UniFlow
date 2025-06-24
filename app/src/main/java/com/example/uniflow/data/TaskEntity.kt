package com.example.uniflow.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vrsta: String,
    val naziv: String,
    val boja: Int,
    val datum: Long,
    val vrijeme: String?
)
