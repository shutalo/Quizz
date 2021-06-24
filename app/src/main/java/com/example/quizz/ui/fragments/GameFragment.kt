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

        CoroutineScope(Dispatchers.IO).launch {
            viewModel.getQuestions()
        }

        viewModel.countDownTick.observe(viewLifecycleOwner){
            binding.timeProgressbar.progress = it
        }
        viewModel.secondsLeft.observe(viewLifecycleOwner){
            binding.timeLeftTv.text = it.toString()
            if(it == 0){
                replaceToGameOverFragment()
                viewModel.gameOver()
            }
        }
        viewModel.questionAnswered.observe(viewLifecycleOwner){
            if(it == true){
                viewModel.startTimer(false)
                questionAnswered = true
                when(radioButtonClicked){
                    1 -> binding.radioButton1.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    2 -> binding.radioButton2.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    3 -> binding.radioButton3.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    4 -> binding.radioButton4.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                }
            } else if(it == false){
                viewModel.startTimer(false)
                questionAnswered = false
                when(radioButtonClicked){
                    1 -> binding.radioButton1.setBackgroundColor(Quizz.context.resources.getColor(R.color.dark_red,Quizz.context.resources.newTheme()))
                    2 -> binding.radioButton2.setBackgroundColor(Quizz.context.resources.getColor(R.color.dark_red,Quizz.context.resources.newTheme()))
                    3 -> binding.radioButton3.setBackgroundColor(Quizz.context.resources.getColor(R.color.dark_red,Quizz.context.resources.newTheme()))
                    4 -> binding.radioButton4.setBackgroundColor(Quizz.context.resources.getColor(R.color.dark_red,Quizz.context.resources.newTheme()))
                }
                when(correctAnswerPosition){
                    1 -> binding.radioButton1.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    2 -> binding.radioButton2.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    3 -> binding.radioButton3.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                    4 -> binding.radioButton4.setBackgroundColor(Quizz.context.resources.getColor(R.color.green,Quizz.context.resources.newTheme()))
                }
            }
        }
        viewModel.questionsFetched.observe(viewLifecycleOwner){
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getNewQuestion()
            }
        }
        viewModel.questionNumber.observe(viewLifecycleOwner){
            if(it >= 1){
                binding.questionNumber.text = "Question " + it.toString()
            }
        }
        viewModel.question.observe(viewLifecycleOwner){
            if(it == null){
                Log.d(TAG,"null")
            } else{
                updateUI(it)
                viewModel.startTimer(true)
            }
        }



        binding.nextQuestion.setOnClickListener{
            if(questionAnswered == false){
                replaceToGameOverFragment()
                viewModel.gameOver()
            } else if(questionAnswered == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.getNewQuestion()
                    viewModel.startTimer(true)
                }
            }

        }
        binding.radioButton1.setOnClickListener {
            viewModel.questionAnswered(binding.question1.text.toString())
            radioButtonClicked = 1
            disableButtons()
        }
        binding.radioButton2.setOnClickListener {
            viewModel.questionAnswered(binding.question2.text.toString())
            radioButtonClicked = 2
            disableButtons()
        }
        binding.radioButton3.setOnClickListener {
            viewModel.questionAnswered(binding.question3.text.toString())
            radioButtonClicked = 3
            disableButtons()
        }
        binding.radioButton4.setOnClickListener {
            viewModel.questionAnswered(binding.question4.text.toString())
            radioButtonClicked = 4
            disableButtons()
        }

    }

    private fun replaceToGameOverFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.game_activity_fragment_container, GameOverFragment()).commit()
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
        binding.radioButton1.isChecked = false
        binding.radioButton2.isClickable = true
        binding.radioButton2.isChecked = false
        binding.radioButton3.isClickable = true
        binding.radioButton3.isChecked = false
        binding.radioButton4.isClickable = true
        binding.radioButton4.isChecked = false
    }

    private fun updateUI(question: Question){
        binding.question.text = question.question
        enableButtons()
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
    }
}