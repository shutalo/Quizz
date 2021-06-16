package com.example.quizz.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.quizz.data.model.User
import kotlinx.android.synthetic.main.leaderboard_item.view.*

class PlayerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(player: User, position: Int){
        itemView.player_name.text = player.username.toString()
        itemView.player_high_score.text = player.highScore.toString()
        itemView.position.text = (position + 4).toString()
    }
}