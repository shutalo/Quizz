package com.example.quizz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizz.Quizz
import com.example.quizz.databinding.FragmentChangePasswordBinding
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel by sharedViewModel<RegisterViewModel>()
    private var backToLoginListener: (()->Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePasswordButton.setOnClickListener{
            viewModel.changePassword(binding.email.text.toString())
            Toast.makeText(requireContext(),"Password reset requested.", Toast.LENGTH_SHORT).show()
        }
        binding.backToLoginButton.setOnClickListener{
            backToLoginListener?.invoke()
        }
    }

    fun setUpBackToLoginListener(listener: ()->Unit){
        backToLoginListener = listener
    }
}