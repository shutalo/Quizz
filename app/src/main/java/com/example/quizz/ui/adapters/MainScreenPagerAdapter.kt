package com.example.quizz.ui.adapters

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.quizz.Quizz
import com.example.quizz.ui.activities.GameActivity
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.fragments.LeaderboardFragment
import com.example.quizz.ui.fragments.MainFragment
import com.example.quizz.ui.fragments.ProfileFragment
import kotlinx.coroutines.awaitCancellation

class MainScreenPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {

    private val username: String = activity.getCurrentUsersUsername()

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> LeaderboardFragment.getInstance(username)
            1 -> MainFragment.getInstance().also {
                it.setUpPlayButtonListener {
                    it.startGameActivity() }
            }
            else -> ProfileFragment.getInstance()
        }
    }

}