package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.databinding.FragmentGameBinding
import com.example.quizz.ui.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameFragment : Fragment() {

    private val viewModel by sharedViewModel<GameViewModel>()
    private lateinit var binding: FragmentGameBinding
    private var questionAnswered = false
    private var gameOverListener: (()->Unit)? = null

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
                gameOverListener?.invoke()
                viewModel.gameOver()
            }
        }
        viewModel.questionAnswered.observe(viewLifecycleOwner){
            questionAnswered = it
        }

        binding.nextQuestion.setOnClickListener{
            if(questionAnswered){
                //get new question and reset timer from viewModel if previous question is answered correctly
            }
        }

    }

    fun setUpGameOverListener(listener: ()->Unit){
        this.gameOverListener = listener
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