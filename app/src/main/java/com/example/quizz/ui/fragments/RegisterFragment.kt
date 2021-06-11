package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.R
import com.example.quizz.databinding.ActivityWelcomeBinding
import com.example.quizz.databinding.FragmentRegisterBinding
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private var listener : (() -> Unit)? = null
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModel<RegisterViewModel>()

    fun setUpListener(listener: (() -> Unit)){
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            binding.registerButton.setOnClickListener{
                viewModel.register(binding.email.text.toString(),binding.password.text.toString())
            }
            binding.switchToLoginButton.setOnClickListener{
                listener?.invoke()
            }

    }
}