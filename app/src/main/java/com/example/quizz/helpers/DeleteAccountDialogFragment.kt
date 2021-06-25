package com.example.quizz.helpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.quizz.databinding.DialogDeleteAccountBinding
import androidx.fragment.app.setFragmentResult

class DeleteAccountDialogFragment: DialogFragment() {

    private lateinit var binding: DialogDeleteAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogDeleteAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.positiveButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("value",true)
            setFragmentResult(DELETE_ACCOUNT_REQUEST,bundle)
            dismiss()
        }

        binding.negativeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object{
        fun getInstance(): DeleteAccountDialogFragment{
            return DeleteAccountDialogFragment()
        }
        const val DELETE_ACCOUNT_REQUEST = "1"
    }
}