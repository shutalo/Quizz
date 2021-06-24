package com.example.quizz.ui.viewmodels

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.data.model.Question
import com.example.quizz.data.repository.Repository
import com.example.quizz.networking.QuestionGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlin.math.ceil

class GameViewModel(private val repository: Repository, private val questionGenerator: QuestionGenerator) : ViewModel() {

    private val TAG = "GameViewModel"

    private var _countDownTick: MutableLiveData<Int> = MutableLiveData()
    var countDownTick: LiveData<Int> = _countDownTick
    private var _secondsLeft: MutableLiveData<Int> = MutableLiveData()
    var secondsLeft: LiveData<Int> = _secondsLeft
    private var _questionAnswered: MutableLiveData<Boolean> = MutableLiveData(null)
    var questionAnswered: LiveData<Boolean> = _questionAnswered
    private var _questionsFetched: MutableLiveData<Boolean> = MutableLiveData(false)
    var questionsFetched: LiveData<Boolean> = _questionsFetched
    private var _questionNumber: MutableLiveData<Int> = MutableLiveData(0)
    var questionNumber: LiveData<Int> = _questionNumber
    private var _question: MutableLiveData<Question> = MutableLiveData(null)
    var question: LiveData<Question> = _question
    private var questions: MutableList<Question> = mutableListOf()
    private lateinit var currentQuestion: Question
    private var firstTimeFetchedQuestions = true
    private var score: Int = 0
    private var finalScore: Int = 0
    private lateinit var timer: CountDownTimer

//    var job = CoroutineScope(Dispatchers.IO).launchPeriodicAsync(30000,{
//        _countDownTick.postValue(it)
//        _secondsLeft.postValue(ceil((it.toDouble() / 1000)).toInt())
//        Log.d(TAG,"akcija1")
//    }) {
//        _secondsLeft.postValue(0)
//        _secondsLeft = MutableLiveData()
//        secondsLeft = _secondsLeft
//        Log.d(TAG,"akcija2")
//    }
//
//    private fun CoroutineScope.launchPeriodicAsync(repeatMillis: Long, action1: (Int) -> Unit,action2: () -> Unit) = this.async {
//        if (repeatMillis > 0) {
//            while (isActive) {
//                action1(repeatMillis.toInt())
//                delay(repeatMillis)
//            }
//        } else {
//            action2()
//        }
//    }
//
//    fun startTimer(){
//        job.start()
//    }
//
//    fun stopTimer(){
//        job.cancel()
//    }

    fun startTimer(value: Boolean){
        viewModelScope.launch {
            if(value){
                createTimer()
            } else {
                timer.cancel()
            }
        }

    }

    private fun createTimer(){
        viewModelScope.launch {
            timer = object: CountDownTimer(30000, 10) {
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

    fun questionAnswered(answer: String){
        viewModelScope.launch {
            if(answer == currentQuestion.correctAnswer) {
                _questionAnswered.postValue(true)
                score++
            } else {
                _questionAnswered.postValue(false)
                _questionNumber.postValue(0)
                finalScore = score
                score = 0
            }
        }
    }

     suspend fun getQuestions(){
         questionGenerator.getQuestions().collect{
             Log.d(TAG,it.toString())
             questions.addAll(it)
             if(firstTimeFetchedQuestions){
                 firstTimeFetchedQuestions = false
                 _questionsFetched.postValue(true)
             }
             Log.d(TAG + "QUESTIONS",questions.toString())
         }
    }

    suspend fun getNewQuestion(){
        if(questions.isEmpty()){
            getQuestions()
        } else{
            currentQuestion = questions.removeLast()
            _questionNumber.postValue(_questionNumber.value?.plus(1))
            _question.postValue(currentQuestion)
        }
    }

    fun getPhoto(): Uri? {
        return repository.getUserFromRoomDatabase().photo
    }

    fun getScore(): Int{
        return finalScore
    }
}