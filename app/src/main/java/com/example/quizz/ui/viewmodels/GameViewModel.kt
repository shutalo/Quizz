package com.example.quizz.ui.viewmodels

import android.net.Uri
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.data.model.Question
import com.example.quizz.data.repository.Repository
import com.example.quizz.helpers.Timer
import com.example.quizz.helpers.TimerState
import com.example.quizz.networking.QuestionGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

class GameViewModel(private val repository: Repository, private val questionGenerator: QuestionGenerator) : ViewModel() {

    private val TAG = "GameViewModel"

    private var _questionAnswered: MutableLiveData<Boolean> = MutableLiveData(null)
    var questionAnswered: LiveData<Boolean> = _questionAnswered
    private var _questionsFetched: MutableLiveData<Boolean> = MutableLiveData(false)
    var questionsFetched: LiveData<Boolean> = _questionsFetched
    private var _questionNumber: MutableLiveData<Int> = MutableLiveData(0)
    var questionNumber: LiveData<Int> = _questionNumber
    private var _question: MutableLiveData<Question> = MutableLiveData(null)
    var question: LiveData<Question> = _question
    private var questions: MutableList<Question> = mutableListOf()
    private var currentQuestion: Question? = null
    private var score: Int = 0
    private var finalScore: Int = 0
    private var _timerTick: MutableLiveData<Int> = MutableLiveData()
    var timerTick: LiveData<Int> = _timerTick
    private val timerIntent = Timer(viewModelScope)
    private val timerStateFlow: StateFlow<TimerState> = timerIntent.timerStateFlow
    var listOfQuestions: LiveData<List<Question>> = questionGenerator.listOfQuestions
    var newQuestionsFetched: Boolean = true


    fun toggleStart(){
        timerIntent.toggleTime(30)
        collectAndUpdateUI()
    }

    private fun collectAndUpdateUI(){
        viewModelScope.launch {
            timerStateFlow.collect {
                _timerTick.postValue(it.secondsRemaining)
            }
        }
    }

     fun gameOver(){
         viewModelScope.launch {
             if(repository.getHighScore() < finalScore){
                 repository.updateHighScore(finalScore)
             }
         }
    }

    fun questionAnswered(answer: String){
        viewModelScope.launch {
            if(answer == currentQuestion?.correctAnswer) {
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
         viewModelScope.launch {
             if(questions.isEmpty()){
                 questionGenerator.getQuestions()
                 newQuestionsFetched = true
             }

                 /*.collect(){
                     val newQuestions = mutableListOf<Question>()
                     it.forEach { q ->
                         val answers = mutableListOf<String>()
                         q.incorrectAnswers.forEach{ ia ->
                             answers.add(Html.fromHtml(ia).toString())
                         }
                         val question = Question(Html.fromHtml(q.question).toString(),Html.fromHtml(q.correctAnswer).toString(),answers,Html.fromHtml(q.difficulty).toString(),Html.fromHtml(q.type).toString(),Html.fromHtml(q.category).toString())
                         newQuestions.add(question)
                     }
                     questions = mutableListOf()
                     questions.addAll(newQuestions)
                     if(firstTimeFetchedQuestions){
                         firstTimeFetchedQuestions = false
                         _questionsFetched.postValue(true)
                     }
                     Log.d(TAG + "QUESTIONS",questions.toString())
                 }*/

             Log.d(TAG + "QUESTIONS",questions.toString())
         }
    }

    fun updateQuestions(listOfQuestions: List<Question>){
       viewModelScope.launch {
           questions = mutableListOf()
           questions.addAll(listOfQuestions)
           _questionsFetched.postValue(true)
//           getNewQuestion()
       }
    }

    suspend fun getNewQuestion(){
        if(questions.isEmpty()){
            getQuestions()
            newQuestionsFetched = true
        } else{
            newQuestionsFetched = false
            Log.d(TAG,"getNewQuestion")
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

    fun checkIfListOfQuestionsIsEmpty(): Boolean{
        return questions.isEmpty()
    }

    fun getNextQuestion(): Question{
        return questions.last()
    }

    fun getCurrentQuestion() :Question{
        return currentQuestion!!
    }
}

