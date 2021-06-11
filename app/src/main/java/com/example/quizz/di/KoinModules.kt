package com.example.quizz.di

import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{
    viewModel<RegisterViewModel> { RegisterViewModel() }
}