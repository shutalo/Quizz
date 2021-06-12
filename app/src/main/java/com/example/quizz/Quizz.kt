package com.example.quizz

import android.app.Application
import com.example.quizz.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Quizz : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        startKoin(){
            androidContext(this@Quizz)
            modules(viewModelModule)
        }
    }

    companion object{
        lateinit var context : Quizz
            private set
    }

}