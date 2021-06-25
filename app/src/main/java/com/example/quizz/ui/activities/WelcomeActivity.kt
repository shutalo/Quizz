package com.example.quizz.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.databinding.ActivityWelcomeBinding
import com.example.quizz.ui.fragments.ChangePasswordFragment
import com.example.quizz.ui.fragments.ChooseUsernameFragment
import com.example.quizz.ui.fragments.LoginFragment
import com.example.quizz.ui.fragments.RegisterFragment
import com.example.quizz.ui.viewmodels.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeActivity :  AppCompatActivity() {

    private val TAG = "WelcomeActivity"
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

        Log.d(TAG,"Started")

        binding = ActivityWelcomeBinding.inflate(layoutInflater)

        registerFragment.setUpLoginButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,loginFragment).commit()
        }
        loginFragment.setUpRegisterButtonListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, registerFragment).commit()
        }
        loginFragment.setUpChangePasswordListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,changePasswordFragment).commit()
        }
        changePasswordFragment.setUpBackToLoginListener {
            supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,loginFragment).commit()
        }

        viewModel.isUsernameChosen.observe(this){
            if(it){
                startMainActivity()
            }
        }
        viewModel.isPasswordChangeRequested.observe(this){
            if(it) {
                Log.d(TAG,"password change requested")
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, loginFragment).commit()
            }
        }
        viewModel.isUserRegisteredSuccessfully.observe(this){
            if(it){
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container,chooseUsernameFragment).commit()
            }
        }
        viewModel.isSigningInSuccessful.observe(this){
            if(it){
                startMainActivity()
            }
        }
        viewModel.isUserSignedIn.observe(this){
            if(it == true){
                startMainActivity()
            } else if(it == false){
                supportFragmentManager.beginTransaction().replace(R.id.welcome_activity_fragment_container, loginFragment).commit()
            }
        }
    }

    private fun startMainActivity(){
        CoroutineScope(Dispatchers.IO).launch {
            val intent: Intent = Intent(Quizz.context,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkIfUserIsSignedIn()
    }
}