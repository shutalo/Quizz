package com.example.quizz.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.R
import com.example.quizz.databinding.FragmentGameOverBinding
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameOverFragment: Fragment() {

    private val TAG = "GameOverFragment"

    private lateinit var binding: FragmentGameOverBinding
    private val viewModel by sharedViewModel<GameViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameOverBinding.inflate(layoutInflater)
        binding.playAgainButton.setOnClickListener{
            replaceToGameFragment()
        }
        binding.leaderboardButton.setOnClickListener{
            switchToLeaderboardScreen()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun replaceToGameFragment(){
        parentFragmentManager.beginTransaction().replace(R.id.game_activity_fragment_container, GameFragment()).commit()
    }

    private fun switchToLeaderboardScreen(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("leaderboard",true)
        startActivity(intent)
    }

    companion object{
        fun getInstance(): GameOverFragment{
            return GameOverFragment()
        }
    }
}