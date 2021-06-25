package com.example.quizz.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.databinding.ActivityGameBinding
import com.example.quizz.ui.fragments.GameFragment
import com.example.quizz.ui.fragments.GameOverFragment
import com.example.quizz.ui.viewmodels.GameViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Retrofit

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel by viewModel<GameViewModel>()
    private var gameFragment: GameFragment = GameFragment.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(binding.gameActivityFragmentContainer.id,gameFragment).commit()
    }



}