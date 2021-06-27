package com.example.quizz.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizz.data.model.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM tokens")
    fun getToken(): Token?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(token: Token)
}