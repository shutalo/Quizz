package com.example.quizz.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizz.Quizz
import com.example.quizz.databinding.FragmentChangePasswordBinding
import com.example.quizz.databinding.FragmentMainBinding
import com.example.quizz.helpers.DeleteAccountDialogFragment
import com.example.quizz.helpers.RulesDialogFragment
import com.example.quizz.ui.activities.GameActivity

class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    private lateinit var binding: FragmentMainBinding
    private var playButtonListener : (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rulesButton.setOnClickListener{
            val dialog = RulesDialogFragment.getInstance()
            dialog.show(parentFragmentManager,TAG)
        }
        binding.playButton.setOnClickListener{
            playButtonListener?.invoke()
        }

    }

    fun setUpPlayButtonListener(listener: ()->Unit){
        this.playButtonListener = listener
    }

    companion object{
        fun getInstance(): MainFragment{
            return MainFragment()
        }
    }

    fun startGameActivity(){
        val intent = Intent(requireContext(), GameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}