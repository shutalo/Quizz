package com.example.quizz.data.repository

import android.util.Log
import android.widget.Toast
import com.example.quizz.Quizz
import com.example.quizz.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

class Repository() {

    private val TAG = "Repository"

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String): Boolean{
        var isRegistrationSuccessful = false
        try {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val highScore = HashMap<String,Int>()
                    highScore["highScore"] = 0
                    database.collection("users").document(getCurrentUser().uid).set(highScore)
                    Log.d(TAG,"User added to database")
                    isRegistrationSuccessful = true
                    Toast.makeText(Quizz.context, "Registered successfully!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.d(TAG,it.message.toString())
                Toast.makeText(Quizz.context, it.message.toString(), Toast.LENGTH_SHORT).show()
            }.await()
        } catch (e: java.lang.Exception){
            Log.d(TAG,e.message.toString())
        }
        return isRegistrationSuccessful
    }

    suspend fun signIn(email: String, password: String): Boolean{
        var signingSuccessful: Boolean = false
        if(email != "" && password != ""){
            try {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "signed in successfully!")
                        signingSuccessful = true
                    }
                }.addOnFailureListener {
                    Log.d(TAG, it.message.toString())
                    Toast.makeText(Quizz.context, it.message.toString(), Toast.LENGTH_SHORT).show()
                }.await()
            } catch (e: Exception){
                Log.d(TAG,e.message.toString())
            }
        }
        return signingSuccessful
    }

    fun checkIfUserIsSignedIn() : Boolean {
        val currentUser : FirebaseUser? = mAuth.currentUser
        return currentUser != null
    }

    fun getCurrentUser(): FirebaseUser {
        return mAuth.currentUser!!
    }

    fun changePassword(email: String){
        mAuth.sendPasswordResetEmail(email)
    }

    private suspend fun checkIfUsernameIsAvailable(username: String): Boolean {
        var isUsernameAvailable = true
        val collection = database.collection("users").get().await()
        collection.forEach{
            if(it.id != getCurrentUser().uid){
                if(it.get("username") == username)
                    isUsernameAvailable = false
            }
        }
        return isUsernameAvailable
    }

    suspend fun chooseUsername(username: String): Boolean{
        return if(checkIfUsernameIsAvailable(username)){
            database.collection("users").document(getCurrentUser().uid).update("username",username)
            true
        } else {
            Toast.makeText(Quizz.context,"Username unavailable!",Toast.LENGTH_SHORT).show()
            false
        }
    }

    suspend fun getCurrentUserObject(): User?{
        val user = database.collection("users").document(getCurrentUser().uid).get().await()
        return user.toObject(User::class.java)
    }

    suspend fun getPlayersForRecyclerView(): List<User>{
        val users: MutableList<User> = mutableListOf()
        val players = database.collection("users").get().await()
        players.forEach {
            users.add(it.toObject(User::class.java))
        }
        users.sortByDescending {
            it.highScore
        }
        for(i in 0..2){
            users.removeAt(0)
        }
        return users
    }

    suspend fun getTopThreePlayers(): List<User>{
        val users: MutableList<User> = mutableListOf()
        val players = database.collection("users").get().await()
        players.forEach {
            users.add(it.toObject(User::class.java))
        }
        users.sortByDescending {
            it.highScore
        }
        val topThreePlayers = mutableListOf<User>()
        for(i in 0..2){
            topThreePlayers.add(users[i])
        }
        return topThreePlayers
    }

//
//    fun setNewHighScore(highScore: Int){
//        val newHighScore = HashMap<String,Int>()
//        newHighScore["highScore"] = highScore
//        database.collection("users").document(getCurrentUser().uid).set(newHighScore)
//    }
//
//    suspend fun getHighScore(): Int{
//        val user = database.collection("users").document(getCurrentUser().uid).get().await()
//        return user.get("highScore") as Int
//    }

}