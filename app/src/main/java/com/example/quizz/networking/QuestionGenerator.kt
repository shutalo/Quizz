package com.example.quizz.networking

import android.text.Html
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizz.Quizz
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
import kotlinx.coroutines.tasks.await

class QuestionGenerator {

    private val TAG = "QuestionGenerator"

    private var _listOfQuestions: MutableLiveData<List<Question>> = MutableLiveData()
    var listOfQuestions: LiveData<List<Question>> = _listOfQuestions

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
}