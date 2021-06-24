package com.example.quizz.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.R
import com.example.quizz.databinding.FragmentGameBinding
import com.example.quizz.ui.viewmodels.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameFragment : Fragment() {

    private val TAG = "GameFragment"

    private val viewModel by sharedViewModel<GameViewModel>()
    private lateinit var binding: FragmentGameBinding
    private var questionAnswered = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            questionAnswered = it
        }

        binding.nextQuestion.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.getQuestions()
            }
            if(questionAnswered){
                //get new question and reset timer from viewModel if previous question is answered correctly
            }
        }
        binding.radioButton1.setOnClickListener {
            viewModel.questionAnswered(0)
        }
        binding.radioButton2.setOnClickListener {
            viewModel.questionAnswered(1)
        }
        binding.radioButton3.setOnClickListener {
            viewModel.questionAnswered(2)
        }
        binding.radioButton4.setOnClickListener {
            viewModel.questionAnswered(3)
        }

    }

    private fun replaceToGameOverFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.game_activity_fragment_container, GameOverFragment()).commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startTimer()
    }

    companion object{
        fun getInstance(): GameFragment{
            return GameFragment()
        }
    }
}