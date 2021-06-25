package com.example.quizz.networking

import com.example.quizz.data.model.Question
import retrofit2.Call
import retrofit2.http.GET

interface QuestionsApi {

    @GET("api.php?amount=10&type=multiple")
    fun getQuestions(): Call<Response>
}