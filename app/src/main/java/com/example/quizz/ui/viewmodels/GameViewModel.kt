package com.example.quizz.ui.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.data.model.Question
import com.example.quizz.data.repository.Repository
import com.example.quizz.networking.QuestionGenerator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.ceil

class GameViewModel(private val repository: Repository, private val questionGenerator: QuestionGenerator) : ViewModel() {

    private val TAG = "GameViewModel"

    private var _countDownTick: MutableLiveData<Int> = MutableLiveData()
    var countDownTick: LiveData<Int> = _countDownTick
    private var _secondsLeft: MutableLiveData<Int> = MutableLiveData()
    var secondsLeft: LiveData<Int> = _secondsLeft
    private var _questionAnswered: MutableLiveData<Boolean> = MutableLiveData()
    var questionAnswered: LiveData<Boolean> = _questionAnswered

    private var score: Int = 0

    fun startTimer(){
        viewModelScope.launch {
            val timer = object: CountDownTimer(10000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    _countDownTick.postValue(millisUntilFinished.toInt())
                    _secondsLeft.postValue(ceil((millisUntilFinished.toDouble() / 1000)).toInt())
                }
                override fun onFinish() {
                    _secondsLeft.postValue(0)
                    _secondsLeft = MutableLiveData()
                    secondsLeft = _secondsLeft
                }
            }
            timer.start()
        }
    }

     fun gameOver(){
         viewModelScope.launch {
             if(repository.getHighScore() < score){
                 repository.updateHighScore(score)
             }
             _questionAnswered.postValue(false)
         }
    }

    fun getNewQuestion(){
        _questionAnswered.postValue(true)
        score++
        //get question from api
    }

    fun questionAnswered(answer: Int){

    }

     suspend fun getQuestions(){
         questionGenerator.getQuestions().collect{
            Log.d(TAG,it.toString())
         }
    }
}