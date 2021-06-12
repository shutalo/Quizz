package com.example.quizz.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.example.quizz.Quizz
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class FirebaseRepository {
    private val TAG = "FirebaseRepository"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(email: String, password: String): Boolean{
        var registrationSuccessful: Boolean = false
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "registered successfully!")
                registrationSuccessful = true
            }
        }.addOnFailureListener {
            Log.d(TAG,it.message.toString())
            Toast.makeText(Quizz.context, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return registrationSuccessful
    }

    fun changePassword(newPassword: String){
        //change password
    }

    fun signIn(email: String, password: String): Boolean{
        var signingSuccessful: Boolean = false
        if(email != "" && password != ""){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "signed in successfully!")
                    signingSuccessful = true
                }
            }.addOnFailureListener {
                Log.d(TAG, it.message.toString())
                Toast.makeText(Quizz.context, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        return signingSuccessful
    }

    fun checkIfUserIsSignedIn() : Boolean {
        val currentUser :FirebaseUser? = mAuth.currentUser
        return currentUser != null
    }

    fun chooseUsername(){
        //check if selected username is available and add it to database
    }

    fun getCurrentUser(): FirebaseUser {
        return mAuth.currentUser!!
    }

    fun signOut(){
        mAuth.signOut()
    }
}