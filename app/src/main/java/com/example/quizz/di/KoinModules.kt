package com.example.quizz.di

import com.example.quizz.data.database.RemoteDatabase
import com.example.quizz.data.repository.Repository
import com.example.quizz.ui.viewmodels.RegisterViewModel
import com.example.quizz.utils.FirebaseService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Repository> { Repository(get(),get()) }
    single<RemoteDatabase> { RemoteDatabase() }
    single<FirebaseService> { FirebaseService() }
}

val viewModelModule = module{
    viewModel<RegisterViewModel> { RegisterViewModel(get()) }
}