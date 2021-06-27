package com.example.quizz.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quizz.R
import com.example.quizz.databinding.FragmentGameOverBinding
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.viewmodels.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GameOverFragment: Fragment() {

    private val TAG = "GameOverFragment"

    private lateinit var binding: FragmentGameOverBinding
    private val viewModel by sharedViewModel<GameViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGameOverBinding.inflate(layoutInflater)
        viewModel.timerFlagForDisablingDoubleEntry = false
        binding.playAgainButton.setOnClickListener{
            replaceToGameFragment()
        }
        binding.leaderboardButton.setOnClickListener{
            switchToLeaderboardScreen()
        }
        binding.scoreTv.text = viewModel.getScore().toString()
        if(viewModel.getPhoto().toString() != "null"){
            Glide.with(binding.root)
                .load(viewModel.getPhoto())
                .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,resources.newTheme()))
                .circleCrop()
                .into(binding.profileImageIv)
        } else {
            Glide.with(binding.root)
                .load(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,resources.newTheme()))
                .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,resources.newTheme()))
                .circleCrop()
                .into(binding.profileImageIv)
        }
        return binding.root
    }

    private fun replaceToGameFragment(){
        parentFragmentManager.beginTransaction().replace(R.id.game_activity_fragment_container, GameFragment.getInstance()).commit()
    }

    private fun switchToLeaderboardScreen(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("leaderboard",true)
        viewModel.saveQuestionsToRoomDatabase()
        startActivity(intent)
    }

    companion object{
        fun getInstance(): GameOverFragment{
            return GameOverFragment()
        }
    }
}