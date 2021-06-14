package com.example.quizz.data.repository

import android.util.Log
import com.example.quizz.data.database.RemoteDatabase
import com.example.quizz.data.model.User
import com.example.quizz.utils.FirebaseService
import com.google.firebase.auth.FirebaseUser

class Repository(private val firebaseDatabase: RemoteDatabase, private val firebaseService: FirebaseService) {

    private val TAG = "Repository"

    fun register(email: String, password: String): Boolean{
        return firebaseService.register(email,password)
    }

    fun signIn(email: String, password: String): Boolean{
        return firebaseService.signIn(email,password)
    }

    fun checkIfUserIsSignedIn() : Boolean {
        return firebaseService.checkIfUserIsSignedIn()
    }

    fun getCurrentUser(): FirebaseUser {
        return firebaseService.getCurrentUser()
    }

    fun signOut(){
        firebaseService.signOut()
    }

    fun changePassword(email: String): Boolean{
        return firebaseService.changePassword(email)
    }

    fun chooseUsername(username: String): Boolean{
        return if (firebaseDatabase.checkIfUsernameIsAvailable(username)){
            firebaseDatabase.chooseUsername(username,firebaseService.getCurrentUser())
            true
        } else {
            false
        }
    }

    fun getCurrentUserObject(): User?{
        return firebaseDatabase.getCurrentUserObject(firebaseService.getCurrentUser())
    }
}