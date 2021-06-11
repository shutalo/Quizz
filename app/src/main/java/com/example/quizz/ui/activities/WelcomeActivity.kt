package com.example.quizz.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.R
import com.example.quizz.databinding.ActivityWelcomeBinding
import com.example.quizz.ui.fragments.ChangePasswordFragment
import com.example.quizz.ui.fragments.LoginFragment
import com.example.quizz.ui.fragments.RegisterFragment
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity :  AppCompatActivity() {

    private val loginFragment: LoginFragment = LoginFragment()
    private val registerFragment: RegisterFragment = RegisterFragment()
    private val changePasswordFragment: ChangePasswordFragment = ChangePasswordFragment()

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        this.supportActionBar?.hide()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, loginFragment).commit()

        registerFragment.setUpListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,loginFragment).commit()
        }
        loginFragment.setUpRegisterButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, registerFragment).commit()
        }
        loginFragment.setUpChangePasswordListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, changePasswordFragment).commit()
        }
    }
}