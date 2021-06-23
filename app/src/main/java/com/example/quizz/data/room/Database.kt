package com.example.quizz.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.quizz.data.model.User
import com.example.quizz.helpers.Converters

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao

    companion object{
        const val NAME = "database"
    }
}