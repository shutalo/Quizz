package com.example.quizz.data.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "questions")
@Parcelize
data class Question(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "question")
    var question: String,
    @ColumnInfo(name = "correct_answer")
    @SerializedName(value = "correct_answer")
    var correctAnswer: String,
    @ColumnInfo(name = "incorrect_answers")
    @SerializedName(value = "incorrect_answers")
    var incorrectAnswers: List<String>,
    @ColumnInfo(name = "difficulty")
    var difficulty: String,
    @ColumnInfo(name = "type")
    var type: String,
    @ColumnInfo(name = "category")
    var category: String
) : Parcelable