package com.example.quizz.networking

import android.text.Html
import android.util.Base64
import android.util.Log
import com.example.quizz.data.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuestionGenerator {

    private val TAG = "QuestionGenerator"

    private val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://opentdb.com/").addConverterFactory(
        GsonConverterFactory.create()).build()

    private val questionsApi: QuestionsApi = retrofit.create(QuestionsApi::class.java)

    suspend fun getQuestions() = flow<List<Question>> {
        try{
            val response = questionsApi.getQuestions()
            Log.d(TAG, response.results.toString())
            response.results.forEach{ it ->
                it.category = Html.fromHtml(it.category).toString()
                it.correctAnswer = Html.fromHtml(it.correctAnswer).toString()
                it.difficulty = Html.fromHtml(it.difficulty).toString()
                it.question = Html.fromHtml(it.question).toString()
                it.type = Html.fromHtml(it.type).toString()
                val answers = mutableListOf<String>()
                it.incorrectAnswers.forEach{
                    answers.add(Html.fromHtml(it).toString())
                }
                for(i in 0..2){
                    it.incorrectAnswers = answers
                }
            }
            emit(response.results)
        } catch (e: Exception){
            Log.d(TAG,e.message.toString())
        }
    }
}