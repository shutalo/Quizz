package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.databinding.FragmentGameOverBinding
import com.example.quizz.ui.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameOverFragment: Fragment() {

    private lateinit var binding: FragmentGameOverBinding
    private val viewModel by sharedViewModel<GameViewModel>()
    private var playAgainButtonListener: (()->Unit)? = null
    private var leaderboardButtonListener: (()->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameOverBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playAgainButton.setOnClickListener{
            playAgainButtonListener?.invoke()
        }
        binding.leaderboardButton.setOnClickListener{
            leaderboardButtonListener?.invoke()
        }
    }

    fun setUpPlayAgainButtonListener(listener: ()->Unit){
        this.playAgainButtonListener = listener
    }

    fun setUpLeaderboardButtonListener(listener: ()->Unit){
        this.leaderboardButtonListener = listener
    }

    companion object{
        fun getInstance(): GameOverFragment{
            return GameOverFragment()
        }
    }
}