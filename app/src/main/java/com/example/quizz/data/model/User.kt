package com.example.quizz.data.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(var username: String? = "", var highScore: Int? = 0) : Parcelable {

}