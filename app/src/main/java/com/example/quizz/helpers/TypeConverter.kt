package com.example.quizz.helpers

import android.net.Uri
import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromStringToUri(value: String?): Uri? {
        return Uri.parse(value)
    }

    @TypeConverter
    fun fromUriToString(uri: Uri?): String {
        return uri.toString()
    }

    @TypeConverter
    fun fromStringToList(value: String): List<String>? {
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListToString(listOfStrings: List<String>): String {
        val gson = Gson()
        return gson.toJson(listOfStrings)
    }
}