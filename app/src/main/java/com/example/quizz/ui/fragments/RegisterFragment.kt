package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.databinding.FragmentRegisterBinding
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment() : Fragment() {

    private val viewModel by sharedViewModel<RegisterViewModel>()
    private var loginListener : (() -> Unit)? = null
    private var registerListener : (() -> Unit)? = null
    private lateinit var binding: FragmentRegisterBinding

    fun setUpLoginButtonListener(listener: (() -> Unit)){
        this.loginListener = listener
    }
    fun setUpRegisterButtonListener(listener: (() -> Unit)){
        this.registerListener = listener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener{
            checkIfRegistrationIsPossible()
        }
        binding.switchToLoginButton.setOnClickListener{
            loginListener?.invoke()
        }
        viewModel.isRegistrationPossible.observe(viewLifecycleOwner) {
            if(it){ registerListener?.invoke() }
        }

    }

    private fun checkIfRegistrationIsPossible() {
        viewModel.register(binding.email.text.toString(),binding.password.text.toString())
    }
}