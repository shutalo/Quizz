package com.example.quizz.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.R
import com.example.quizz.data.model.User

class LeaderboardRecyclerViewAdapter(private var players: List<User>): RecyclerView.Adapter<PlayerViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position],position)
    }

    fun refreshData(players: List<User>) {
        this.players = players
        this.notifyDataSetChanged()
    }
}