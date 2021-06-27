package com.example.quizz.di

import com.example.quizz.data.repository.Repository
import com.example.quizz.data.room.*
import com.example.quizz.networking.QuestionGenerator
import com.example.quizz.ui.viewmodels.GameViewModel
import com.example.quizz.ui.viewmodels.MainScreenViewModel
import com.example.quizz.ui.viewmodels.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    single<Repository> { Repository(get(),get()) }
    single<Database> { DatabaseBuilder.getInstance() }
    single<Dao> { get<Database>().dao() }
    single<TokenDao> { get<Database>().tokenDao() }
    //single<QuestionsDao> { get<Database>().questionsDao() }
    single<QuestionGenerator> { QuestionGenerator() }
}

val viewModelModules = module{
    viewModel<RegisterViewModel> { RegisterViewModel(get()) }
    viewModel<MainScreenViewModel> { MainScreenViewModel(get()) }
    viewModel<GameViewModel> { GameViewModel(get(),get()) }
}