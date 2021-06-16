package com.example.quizz.di

import com.example.quizz.data.repository.Repository
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Repository> { Repository() }
}

val viewModelModule = module{
    viewModel<RegisterViewModel> { RegisterViewModel(get()) }
    viewModel<MainScreenViewModel> { MainScreenViewModel(get()) }
}