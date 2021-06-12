package com.example.quizz.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizz.R
import com.example.quizz.databinding.FragmentLoginBinding
import com.example.quizz.databinding.FragmentRegisterBinding
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment() : Fragment() {

    private val viewModel by sharedViewModel<RegisterViewModel>()
    private var registerButtonListener : (() -> Unit)? = null
    private var changePasswordListener : (() -> Unit)? = null
    private var loginButtonListener : (() -> Unit)? = null
    private lateinit var binding: FragmentLoginBinding

    fun setUpRegisterButtonListener(listener: (() -> Unit)){
        this.registerButtonListener = listener
    }

    fun setUpChangePasswordListener(listener: (() -> Unit)){
        this.changePasswordListener = listener
    }

    fun setUpLoginButtonListener(listener: (() -> Unit)){
        this.loginButtonListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchToRegisterButton.setOnClickListener{
            registerButtonListener?.invoke()
        }
        binding.forgotPasswordTv.setOnClickListener{
            changePasswordListener?.invoke()
        }
        binding.loginButton.setOnClickListener{
            viewModel.signIn(binding.email.text.toString(),binding.password.text.toString())
        }

        viewModel.isSigningInSuccessful.observe(viewLifecycleOwner){
            if(it){
                loginButtonListener?.invoke()
            }
        }
    }
}