package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.databinding.FragmentChooseUsernameBinding
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChooseUsernameFragment : Fragment() {

    private val viewModel by sharedViewModel<RegisterViewModel>()
    private lateinit var binding: FragmentChooseUsernameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChooseUsernameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseUsernameButton.setOnClickListener{
            viewModel.chooseUsername(binding.username.text.toString())
        }

    }
}