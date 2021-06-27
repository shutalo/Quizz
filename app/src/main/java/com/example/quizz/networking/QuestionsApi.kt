package com.example.quizz.networking

import com.example.quizz.data.model.Question
import com.example.quizz.data.model.Token
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface QuestionsApi {

    @GET("api.php?amount=50&type=multiple")
    fun getQuestions(): Call<Response>

    @GET("api_token.php?command=request")
    fun getToken(): Call<Token>

    @GET("api.php?amount=50&type=multiple")
    fun getQuestionsWithToken(@Query("token") token: String): Call<Response>

    @GET("api.php?amount=50&type=multiple")
    fun checkIfTokenIsValid(@Query("token") token: String): Call<Response>
}