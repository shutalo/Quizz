package com.example.quizz.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.databinding.ActivityGameBinding
import com.example.quizz.ui.fragments.GameFragment
import com.example.quizz.ui.fragments.GameOverFragment
import com.example.quizz.ui.viewmodels.GameViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel by viewModel<GameViewModel>()
    private var gameFragment: GameFragment = GameFragment.getInstance()
    private val gameOverFragment: GameOverFragment = GameOverFragment.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()
        gameFragment.setUpGameOverListener {
            supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id,gameOverFragment).commit()
        }
        gameOverFragment.setUpPlayAgainButtonListener {
            gameFragment = GameFragment.getInstance()
            supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id,gameFragment).commit()
        }
        gameOverFragment.setUpLeaderboardButtonListener {
            switchToLeaderboardScreen()
        }

        supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id,gameFragment).commit()
    }

    private fun switchToLeaderboardScreen(){
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("leaderboard",true)
        startActivity(intent)
    }

}