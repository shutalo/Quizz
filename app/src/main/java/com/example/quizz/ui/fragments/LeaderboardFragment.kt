package com.example.quizz.ui.fragments

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.databinding.FragmentLeaderboardBinding
import com.example.quizz.databinding.FragmentMainBinding
import com.example.quizz.helpers.ImageParser
import com.example.quizz.ui.adapters.LeaderboardRecyclerViewAdapter
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.example.quizz.ui.viewmodels.RegisterViewModel
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.fragment_choose_username.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LeaderboardFragment: Fragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var username: String
    private val viewModel by sharedViewModel<MainScreenViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.usersForRecyclerViewFetched.observe(viewLifecycleOwner){
            (binding.recyclerView.adapter as LeaderboardRecyclerViewAdapter).refreshData(it,username)
        }
        viewModel.topThreePlayers.observe(viewLifecycleOwner){
            it[0].photo?.let { it1 -> setUpImage(it1,binding.playerPosition1Iv) }
            it[1].photo?.let { it2 -> setUpImage(it2,binding.playerPosition2Iv) }
            it[2].photo?.let { it3 -> setUpImage(it3,binding.playerPosition3Iv) }
            binding.playerHighScore1.text = it[0].highScore.toString()
            binding.playerHighScore2.text = it[1].highScore.toString()
            binding.playerHighScore3.text = it[2].highScore.toString()
            binding.playerName1.text = it[0].username
            binding.playerName2.text = it[1].username
            binding.playerName3.text = it[2].username
        }
        setUpRecyclerView()
    }

    companion object{
        fun getInstance(username: String): LeaderboardFragment{
            val lf = LeaderboardFragment()
            lf.username = username
            return lf
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

    private fun setUpImage(imageUri: Uri,view: ImageView){
        val theme = resources.newTheme()
        Glide.with(binding.root)
            .load(imageUri)
            .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,theme))
            .circleCrop()
            .into(view)
    }
}