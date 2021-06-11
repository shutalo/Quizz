package com.example.quizz.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.R
import com.example.quizz.databinding.ActivityWelcomeBinding
import com.example.quizz.ui.fragments.ChangePasswordFragment
import com.example.quizz.ui.fragments.ChooseUsernameFragment
import com.example.quizz.ui.fragments.LoginFragment
import com.example.quizz.ui.fragments.RegisterFragment
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity :  AppCompatActivity() {

    private val viewModel by viewModel<RegisterViewModel>()
    private val loginFragment: LoginFragment = LoginFragment()
    private val registerFragment: RegisterFragment = RegisterFragment()
    private val changePasswordFragment: ChangePasswordFragment = ChangePasswordFragment()
    private val chooseUsernameFragment: ChooseUsernameFragment = ChooseUsernameFragment()

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        this.supportActionBar?.hide()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, loginFragment).commit()

        registerFragment.setUpLoginButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,loginFragment).commit()
        }
        registerFragment.setUpRegisterButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,chooseUsernameFragment).commit()
        }
        loginFragment.setUpRegisterButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, registerFragment).commit()
        }
        loginFragment.setUpChangePasswordListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, changePasswordFragment).commit()
        }
        changePasswordFragment.setUpChangePasswordListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, loginFragment).commit()
        }

        viewModel.isUserSignedIn.observe(this){
            val intent: Intent = Intent()
            //mozda izmjenit da ne salje firebase user nego moju klasu usera
            intent.putExtra("user",viewModel.getCurrentUser())
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkIfUserIsSignedIn()
    }
}