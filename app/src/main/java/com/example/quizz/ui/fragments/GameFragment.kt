package com.example.quizz.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.data.model.Question
import com.example.quizz.databinding.FragmentGameBinding
import com.example.quizz.ui.viewmodels.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import android.os.CountDownTimer
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.flow.collect
import java.util.*

class GameFragment : Fragment() {

    private val TAG = "GameFragment"

    private val viewModel by sharedViewModel<GameViewModel>()
    private lateinit var binding: FragmentGameBinding
    private var questionAnswered: Boolean? = null
    private var radioButtonClicked = 0
    private var correctAnswerPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = viewModel.getTokenFromRoomDatabase()

        CoroutineScope(Dispatchers.IO).launch {
            if(viewModel.checkIfListOfQuestionsIsEmpty()){
                if(token != null){
                    viewModel.getQuestions(token)
                } else {
                    Log.d(TAG,"token is null")
                    viewModel.getTokenFromApi()
                }
            }
        }

        viewModel.listOfQuestions.observe(viewLifecycleOwner){
            if(viewModel.checkIfListOfQuestionsIsEmpty() && viewModel.newQuestionsFetched && it != null){
                viewModel.updateQuestions(it)
            }
        }
        viewModel.questionAnswered.observe(viewLifecycleOwner){
            if(it == true){
                viewModel.toggleStart()
                questionAnswered = true
                when(radioButtonClicked){
                    1 ->{
                        binding.radioButton1.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton1.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_answered_correctly,resources.newTheme())
                    }
                    2 ->{
                        binding.radioButton2.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton2.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_answered_correctly,resources.newTheme())
                    }
                    3 ->{
                        binding.radioButton3.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton3.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_answered_correctly,resources.newTheme())
                    }
                    4 ->{
                        binding.radioButton4.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton4.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_answered_correctly,resources.newTheme())
                    }
                }
            } else if(it == false){
                viewModel.toggleStart()
                questionAnswered = false
                when(radioButtonClicked){
                    1 ->{
                        binding.radioButton1.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton1.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_incorrect_answer,resources.newTheme())
                    }
                    2 ->{
                        binding.radioButton2.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton2.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_incorrect_answer,resources.newTheme())
                    }
                    3 ->{
                        binding.radioButton3.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton3.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_incorrect_answer,resources.newTheme())
                    }
                    4 ->{
                        binding.radioButton4.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton4.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_incorrect_answer,resources.newTheme())
                    }
                }
                when(correctAnswerPosition){
                    1 ->{
                        binding.radioButton1.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton1.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_correct_answer,resources.newTheme())
                    }
                    2 ->{
                        binding.radioButton2.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton2.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_correct_answer,resources.newTheme())
                    }
                    3 ->{
                        binding.radioButton3.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton3.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_correct_answer,resources.newTheme())
                    }
                    4 ->{
                        binding.radioButton4.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
                        binding.radioButton4.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_correct_answer,resources.newTheme())
                    }
                }
            }
        }
        viewModel.questionsFetched.observe(viewLifecycleOwner){
            CoroutineScope(Dispatchers.Main).launch {
                if(it){
                    viewModel.getNewQuestion()
                }
            }
        }
        viewModel.questionNumber.observe(viewLifecycleOwner){
            if(it >= 1){
                binding.questionNumber.text = "Question " + it.toString()
            }
        }
        viewModel.question.observe(viewLifecycleOwner){
            if(it == null){
                Log.d("$TAG question observe","null")
            } else{
                updateUI(it)
                viewModel.toggleStart()
            }
        }

        viewModel.timerTick.observe(viewLifecycleOwner){
            if(it != 0 && it != null){
                updateTimerUi(it)
            } else if(it == 0 && questionAnswered == null && viewModel.timerFlagForDisablingDoubleEntry){
                updateTimerUi(it)
                viewModel.gameOver()
                replaceToGameOverFragment()
            }
        }



        binding.nextQuestion.setOnClickListener{
            if(questionAnswered == false){
                questionAnswered = null
                viewModel.gameOver()
                replaceToGameOverFragment()
            } else if(questionAnswered == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    questionAnswered = null
                    viewModel.getNewQuestion()
                }
            }

        }
        binding.radioButton1.setOnClickListener {
            radioButtonClicked = 1
            viewModel.questionAnswered(binding.question1.text.toString())
            disableButtons()
        }
        binding.radioButton2.setOnClickListener {
            radioButtonClicked = 2
            viewModel.questionAnswered(binding.question2.text.toString())
            disableButtons()
        }
        binding.radioButton3.setOnClickListener {
            radioButtonClicked = 3
            viewModel.questionAnswered(binding.question3.text.toString())
            disableButtons()
        }
        binding.radioButton4.setOnClickListener {
            radioButtonClicked = 4
            viewModel.questionAnswered(binding.question4.text.toString())
            disableButtons()
        }

    }

    private fun replaceToGameOverFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.game_activity_fragment_container, GameOverFragment.getInstance()).commit()
    }

    companion object{
        fun getInstance(): GameFragment{
            return GameFragment()
        }
    }

    private fun disableButtons(){
        binding.radioButton1.isClickable = false
        binding.radioButton2.isClickable = false
        binding.radioButton3.isClickable = false
        binding.radioButton4.isClickable = false
    }

    private fun enableButtons(){
        binding.radioButton1.isClickable = true
        binding.radioButton2.isClickable = true
        binding.radioButton3.isClickable = true
        binding.radioButton4.isClickable = true
        binding.radioButton1.buttonDrawable = ResourcesCompat.getDrawable(resources,R.color.background_color,resources.newTheme())
        binding.radioButton1.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_regular_radio_button,resources.newTheme())
        binding.radioButton2.buttonDrawable = null
        binding.radioButton2.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_regular_radio_button,resources.newTheme())
        binding.radioButton3.buttonDrawable = null
        binding.radioButton3.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_regular_radio_button,resources.newTheme())
        binding.radioButton4.buttonDrawable = null
        binding.radioButton4.background = ResourcesCompat.getDrawable(resources,R.drawable.ic_regular_radio_button,resources.newTheme())
    }

    private fun resetButtons(){
        binding.radioButton1.isChecked = false
        binding.radioButton2.isChecked = false
        binding.radioButton3.isChecked = false
        binding.radioButton4.isChecked = false
    }

    private fun updateUI(question: Question){
        binding.question.text = question.question
        resetButtons()
        correctAnswerPosition = (1..4).random()
        when(correctAnswerPosition){
            1 -> {
                binding.question1.text = question.correctAnswer
                binding.question2.text = question.incorrectAnswers[0]
                binding.question3.text = question.incorrectAnswers[1]
                binding.question4.text = question.incorrectAnswers[2]
            }
            2 -> {
                binding.question1.text = question.incorrectAnswers[0]
                binding.question2.text = question.correctAnswer
                binding.question3.text = question.incorrectAnswers[1]
                binding.question4.text = question.incorrectAnswers[2]
            }
            3 -> {
                binding.question1.text = question.incorrectAnswers[0]
                binding.question2.text = question.incorrectAnswers[1]
                binding.question3.text = question.correctAnswer
                binding.question4.text = question.incorrectAnswers[2]
            }
            4 -> {
                binding.question1.text = question.incorrectAnswers[0]
                binding.question2.text = question.incorrectAnswers[1]
                binding.question3.text = question.incorrectAnswers[2]
                binding.question4.text = question.correctAnswer
            }
        }
        enableButtons()
    }

    private fun updateTimerUi(value: Int){
        binding.timeLeftTv.text = value.toString()
        binding.timeProgressbar.progress = value * 1000
    }

    override fun onResume() {
        super.onResume()
    }
}