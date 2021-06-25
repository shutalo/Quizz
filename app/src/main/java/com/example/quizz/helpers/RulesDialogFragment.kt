package com.example.quizz.helpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.quizz.databinding.DialogDeleteAccountBinding
import com.example.quizz.databinding.DialogRulesBinding

class RulesDialogFragment : DialogFragment(){

    private lateinit var binding: DialogRulesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogRulesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exit.setOnClickListener {
            dismiss()
        }
    }

    companion object{
        fun getInstance(): RulesDialogFragment{
            return RulesDialogFragment()
        }
    }
}