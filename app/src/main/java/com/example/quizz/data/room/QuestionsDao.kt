package com.example.quizz.data.room

import androidx.room.*
import androidx.room.Dao
import com.example.quizz.data.model.Question
import com.example.quizz.data.model.Token

@Dao
interface QuestionsDao {

    @Query("SELECT * FROM questions")
    fun getQuestions(): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Query("DELETE FROM questions")
    fun deleteAll()
}