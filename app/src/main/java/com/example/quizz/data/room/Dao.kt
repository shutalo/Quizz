package com.example.quizz.data.room

import androidx.room.*
import androidx.room.Dao
import com.example.quizz.data.model.User

@Dao
interface Dao {

    @Query("SELECT * FROM user")
    fun getUser(): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun deleteUser(user: User)
}