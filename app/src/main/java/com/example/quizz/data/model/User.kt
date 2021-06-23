package com.example.quizz.data.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "user")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "username")
    var username: String = "",
    @ColumnInfo(name = "highScore")
    var highScore: Int? = 0,
    @ColumnInfo(name = "photo")
    var photo: Uri? = null
) : Parcelable