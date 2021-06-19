package com.example.quizz.ui.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.data.model.User
import kotlinx.android.synthetic.main.leaderboard_item.view.*

class LeaderboardRecyclerViewAdapter(private var players: List<User>): RecyclerView.Adapter<PlayerViewHolder>()  {

    private lateinit var username: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        if(players[position].username == username){
            holder.itemView.player_name.setTextColor(Quizz.context.resources.getColor(R.color.pink1,Quizz.context.resources.newTheme()))
            holder.itemView.player_high_score.setTextColor(Quizz.context.resources.getColor(R.color.pink1,Quizz.context.resources.newTheme()))
            holder.itemView.player_position_iv.setImageResource(R.drawable.current_user_recycler_view_item_image)
            holder.itemView.position.setTextColor(Quizz.context.resources.getColor(R.color.white,Quizz.context.resources.newTheme()))
            holder.bind(players[position],position)
        } else {
            holder.bind(players[position],position)
        }
    }

    fun refreshData(players: List<User>,username: String) {
        this.username = username
        this.players = players
        this.notifyDataSetChanged()
    }

}