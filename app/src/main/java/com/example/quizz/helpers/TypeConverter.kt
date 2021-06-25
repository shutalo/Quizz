package com.example.quizz.helpers

import android.net.Uri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String?): Uri? {
        return Uri.parse(value)
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String {
        return uri.toString()
    }
}