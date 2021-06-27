package com.example.quizz.networking

import android.text.Html
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizz.Quizz
import com.example.quizz.data.model.Question
import com.example.quizz.data.model.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.tasks.await

class QuestionGenerator {

    private val TAG = "QuestionGenerator"

    private var _listOfQuestions: MutableLiveData<List<Question>> = MutableLiveData()
    var listOfQuestions: LiveData<List<Question>> = _listOfQuestions
    private var _token: MutableLiveData<Token> = MutableLiveData()
    var token: LiveData<Token> = _token

    private val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://opentdb.com/").addConverterFactory(
        GsonConverterFactory.create()).build()

    private val questionsApi: QuestionsApi = retrofit.create(QuestionsApi::class.java)

    fun getQuestions(){
        try{
            val response = questionsApi.getQuestions()
            response.enqueue(object: Callback<com.example.quizz.networking.Response>{

                override fun onResponse(call: Call<com.example.quizz.networking.Response>, response: Response<com.example.quizz.networking.Response>) {
                    if(response.code() != 200){
                        Log.d(TAG,"code is: " + response.code().toString())
                        Toast.makeText(Quizz.context,"Could not fetch questions: code: ${response.code()}",Toast.LENGTH_SHORT).show()
                    } else {
                        CoroutineScope(Dispatchers.IO).launch{
                            val myResponse: com.example.quizz.networking.Response = response.body()!!

                            val newQuestions = mutableListOf<Question>()
                            myResponse.results.forEach { q ->
                                val answers = mutableListOf<String>()
                                q.incorrectAnswers.forEach{ ia ->
                                    answers.add(Html.fromHtml(ia).toString())
                                }
                                val question = Question(Html.fromHtml(q.question).toString(),Html.fromHtml(q.correctAnswer).toString(),answers,Html.fromHtml(q.difficulty).toString(),Html.fromHtml(q.type).toString(),Html.fromHtml(q.category).toString())
                                newQuestions.add(question)
                            }
                            _listOfQuestions.postValue(newQuestions)
                        }
                    }
                }

                override fun onFailure(call: Call<com.example.quizz.networking.Response>, t: Throwable) {
                    Log.d(TAG,t.message.toString())
                }
            })
        } catch (e: Exception){
            Log.d("$TAG exception",e.message.toString())
        }
    }

    fun getQuestionsWithToken(token: String){
        try{
            val response = questionsApi.getQuestionsWithToken(token)
            response.enqueue(object: Callback<com.example.quizz.networking.Response>{

                override fun onResponse(call: Call<com.example.quizz.networking.Response>, response: Response<com.example.quizz.networking.Response>) {
                    if(response.code() != 200){
                        Log.d(TAG,"code is: " + response.code().toString())
                        Toast.makeText(Quizz.context,"Could not fetch questions: code: ${response.code()}",Toast.LENGTH_SHORT).show()

                        // if token too old generate new one
                    } else {
                        CoroutineScope(Dispatchers.IO).launch{
                            val myResponse: com.example.quizz.networking.Response = response.body()!!

                            val newQuestions = mutableListOf<Question>()
                            myResponse.results.forEach { q ->
                                val answers = mutableListOf<String>()
                                q.incorrectAnswers.forEach{ ia ->
                                    answers.add(Html.fromHtml(ia).toString())
                                }
                                val question = Question(Html.fromHtml(q.question).toString(),Html.fromHtml(q.correctAnswer).toString(),answers,Html.fromHtml(q.difficulty).toString(),Html.fromHtml(q.type).toString(),Html.fromHtml(q.category).toString())
                                newQuestions.add(question)
                            }
                            _listOfQuestions.postValue(newQuestions)
                        }
                    }
                }

                override fun onFailure(call: Call<com.example.quizz.networking.Response>, t: Throwable) {
                    Log.d(TAG,t.message.toString())
                }
            })
        } catch (e: Exception){
            Log.d("$TAG exception",e.message.toString())
        }
    }

    fun getToken(){
        try {
            questionsApi.getToken().enqueue(object: Callback<Token>{
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if(response.code() != 200){
                        Log.d(TAG,"code is: " + response.code().toString())
                        Toast.makeText(Quizz.context,"Could not fetch token: code: ${response.code()}",Toast.LENGTH_SHORT).show()
                    } else {
                        val myToken: Token = Token(response.body()!!.token)
                        Log.d(TAG,myToken.token.toString())
                        _token.postValue(myToken)
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Log.d(TAG,t.message.toString())
                }

            })
        } catch (e: Exception){
            Log.d(TAG,e.message.toString())
        }
    }

    fun checkIfTokenIsValid(token: Token){
        try {
            questionsApi.checkIfTokenIsValid(token.token).enqueue(object: Callback<com.example.quizz.networking.Response>{
                override fun onResponse(
                    call: Call<com.example.quizz.networking.Response>,
                    response: Response<com.example.quizz.networking.Response>
                ) {
                    if(response.code() == 3){
                        Log.d(TAG,response.code().toString())
                    } else {
                        Log.d(TAG,response.code().toString())
                        _token.postValue(token)
                    }
                }

                override fun onFailure(
                    call: Call<com.example.quizz.networking.Response>,
                    t: Throwable
                ) {
                    Log.d(TAG,t.message.toString())
                }

            })
        } catch (e: Exception){
            Log.d(TAG,e.message.toString())
        }
    }
}