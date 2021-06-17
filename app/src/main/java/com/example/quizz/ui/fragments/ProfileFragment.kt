package com.example.quizz.ui.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizz.databinding.FragmentMainBinding
import com.example.quizz.databinding.FragmentProfileBinding
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.activities.WelcomeActivity
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment() : Fragment() {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val viewModel by sharedViewModel<MainScreenViewModel>()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signOutBtn.setOnClickListener{
            mAuth.signOut()
            startWelcomeActivity()
        }

        binding.changePasswordButton.setOnClickListener{
           viewModel.updatePassword(binding.newPassword.text.toString(),binding.confirmPassword.text.toString())
        }

        binding.deleteAccountTv.setOnClickListener{
            viewModel.deleteAccount()
        }

        binding.editProfileImageIv.setOnClickListener{
            viewModel.takePhoto(activity as MainActivity)
        }

        viewModel.accountDeleted.observe(viewLifecycleOwner){
            if(it){
                viewModel.signOut()
                startWelcomeActivity()
            }
        }

        viewModel.imageUpdated.observe(viewLifecycleOwner){
//            binding.profileImageIv.setImageBitmap(viewModel.getPhoto())
        }
    }


    private fun startWelcomeActivity(){
        CoroutineScope(Dispatchers.IO).launch {
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    companion object{
        fun getInstance(): ProfileFragment{
            return ProfileFragment()
        }
    }
}