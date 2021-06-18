package com.example.quizz.ui.fragments

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.databinding.FragmentLeaderboardBinding
import com.example.quizz.databinding.FragmentMainBinding
import com.example.quizz.helpers.ImageParser
import com.example.quizz.ui.adapters.LeaderboardRecyclerViewAdapter
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.example.quizz.ui.viewmodels.RegisterViewModel
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LeaderboardFragment: Fragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    private val viewModel by sharedViewModel<MainScreenViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.usersForRecyclerViewFetched.observe(viewLifecycleOwner){
            (binding.recyclerView.adapter as LeaderboardRecyclerViewAdapter).refreshData(it)
        }
        viewModel.topThreePlayers.observe(viewLifecycleOwner){
            val player1 = RoundedBitmapDrawableFactory.create(resources,ImageParser.byteArrayToBitMap(it[0].photo))
            player1.isCircular = true
            binding.playerPosition1Iv.setImageDrawable(player1)
            val player2 = RoundedBitmapDrawableFactory.create(resources,ImageParser.byteArrayToBitMap(it[1].photo))
            player2.isCircular = true
            binding.playerPosition2Iv.setImageDrawable(player2)
            val player3 = RoundedBitmapDrawableFactory.create(resources,ImageParser.byteArrayToBitMap(it[2].photo))
            player3.isCircular = true
            binding.playerPosition3Iv.setImageDrawable(player3)
            binding.playerHighScore1.text = it[0].highScore.toString()
            binding.playerHighScore2.text = it[1].highScore.toString()
            binding.playerHighScore3.text = it[2].highScore.toString()
            binding.playerName1.text = it[0].username.toString()
            binding.playerName2.text = it[1].username.toString()
            binding.playerName3.text = it[2].username.toString()
        }
        setUpRecyclerView()
    }

    companion object{
        fun getInstance(): LeaderboardFragment{
            return LeaderboardFragment()
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = LeaderboardRecyclerViewAdapter(mutableListOf())
            viewModel.getPlayersForRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTopThreePlayers()
        viewModel.getPlayersForRecyclerView()
    }
}