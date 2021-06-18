package com.example.quizz.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
            viewModel.signOut()
            startWelcomeActivity()
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
                viewModel.signOut()
                startWelcomeActivity()
            }
        }

        viewModel.imageUpdated.observe(viewLifecycleOwner){
            if(it != null /*&& !ImageParser.bitMapToByteArray(it).contentEquals(ImageParser.bitMapToByteArray(BitmapFactory.decodeResource(Quizz.context.resources,R.drawable.profile_photo)))*/){
                binding.editProfileImageIv.setImageResource(R.drawable.ic_pen)
//                val img = RoundedBitmapDrawableFactory.create(resources,it)
//                img.isCircular = true
//                binding.profileImageIv.setImageDrawable(img)
                val theme = resources.newTheme()
                Glide.with(binding.root)
                    .load(it)
                    .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,theme))
                    .circleCrop()
                    .into(binding.profileImageIv)
            } else {
                val theme = resources.newTheme()
                binding.profileImageIv.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.profile_image,theme))
            }
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