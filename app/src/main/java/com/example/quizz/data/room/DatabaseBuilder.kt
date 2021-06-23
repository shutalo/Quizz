package com.example.quizz.data.room

import android.provider.ContactsContract
import androidx.room.Room
import com.example.quizz.Quizz

object DatabaseBuilder {

    private var instance: Database? = null

    fun getInstance(): Database{
        synchronized(Database::class){
            if(instance == null){
                instance = buildDatabase()
            }
        }
        return instance!!
    }

    private fun buildDatabase(): Database {
        return Room.databaseBuilder(
            Quizz.context,Database::class.java,Database.NAME
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
    }
}