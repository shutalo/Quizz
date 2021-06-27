package com.example.quizz.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tokens")
data class Token(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "token")
    var token: String)