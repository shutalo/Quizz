package com.example.quizz.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.quizz.R
import com.example.quizz.databinding.FragmentProfileBinding
import com.example.quizz.helpers.DeleteAccountDialogFragment
import com.example.quizz.ui.activities.MainActivity
import com.example.quizz.ui.activities.WelcomeActivity
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ProfileFragment() : Fragment() {

    private val TAG = "ProfileFragment"
    private val viewModel by sharedViewModel<MainScreenViewModel>()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signOutBtn.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.signOut()
                startWelcomeActivity()
            }
        }

        binding.changePasswordButton.setOnClickListener{
           viewModel.updatePassword(binding.newPassword.text.toString(),binding.confirmPassword.text.toString())
        }

        binding.deleteAccountTv.setOnClickListener{
            val dialog = DeleteAccountDialogFragment.getInstance()
            parentFragmentManager.setFragmentResultListener(DeleteAccountDialogFragment.DELETE_ACCOUNT_REQUEST,viewLifecycleOwner,
                { requestKey, result ->
                    if(requestKey == DeleteAccountDialogFragment.DELETE_ACCOUNT_REQUEST){
                        if(result.getBoolean("value")){
                            viewModel.deleteAccount()
                        }
                    }
                })
            dialog.show(parentFragmentManager,TAG)
        }

        binding.editProfileImageIv.setOnClickListener{
            viewModel.takePhoto(activity as MainActivity)
        }

        viewModel.accountDeleted.observe(viewLifecycleOwner){
            if(it){
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.signOut()
                    startWelcomeActivity()
                }
            }
        }

        viewModel.imageUpdated.observe(viewLifecycleOwner){
            if(it != null){
                binding.editProfileImageIv.setImageResource(R.drawable.ic_pen)
            } else {
                binding.editProfileImageIv.setImageResource(R.drawable.ic_camera)
            }
            Glide.with(binding.root)
                .load(it)
                .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,resources.newTheme()))
                .circleCrop()
                .into(binding.profileImageIv)
        }

        viewModel.photoUploadStarted.observe(viewLifecycleOwner){
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            } else{
                binding.progressBar.visibility = View.GONE
            }
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