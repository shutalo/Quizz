package com.example.quizz.data.room

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizz.data.model.User

@Dao
interface Dao {

    @Query("SELECT * FROM user")
    fun getUser(): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun deleteUser(user: User)


//    @Query("UPDATE user SET photo=:photo WHERE username=:username")
//    fun updatePhoto(photo: Uri,username: String)
}