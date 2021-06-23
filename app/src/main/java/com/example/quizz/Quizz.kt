package com.example.quizz

import android.app.Application
import com.example.quizz.di.appModules
import com.example.quizz.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Quizz : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        startKoin(){
            androidContext(this@Quizz)
            modules(viewModelModules, appModules)
        }
    }

    companion object{
        lateinit var context : Quizz
            private set
    }

}