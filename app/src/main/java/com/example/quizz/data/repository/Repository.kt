package com.example.quizz.data.repository

import android.util.Log
import android.widget.Toast
import com.example.quizz.Quizz
import com.example.quizz.data.database.RemoteDatabase
import com.example.quizz.utils.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class Repository(private val firebaseDatabase: RemoteDatabase, private val firebaseService: FirebaseService) {
    fun register(email: String, password: String): Boolean{
        return firebaseService.register(email,password)
    }

    fun changePassword(newPassword: String){
        //change password
    }

    fun signIn(email: String, password: String): Boolean{
        return firebaseService.signIn(email,password)
    }

    fun checkIfUserIsSignedIn() : Boolean {
        return firebaseService.checkIfUserIsSignedIn()
    }

    fun chooseUsername(){
        //check if selected username is available and add it to database
    }

    fun getCurrentUser(): FirebaseUser {
        return firebaseService.getCurrentUser()
    }

    fun signOut(){
        firebaseService.signOut()
    }
}