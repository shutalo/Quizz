package com.example.quizz.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.fragments.LeaderboardFragment
import com.example.quizz.ui.fragments.MainFragment
import com.example.quizz.ui.fragments.ProfileFragment

class MainScreenPagerAdapter(activity: MainActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> LeaderboardFragment.getInstance()
            1 -> MainFragment.getInstance()
            else -> ProfileFragment.getInstance()
        }
    }
}