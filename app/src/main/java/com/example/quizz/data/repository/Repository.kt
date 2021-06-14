package com.example.quizz.data.repository

import android.util.Log
import android.widget.Toast
import com.example.quizz.Quizz
import com.example.quizz.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class Repository() {

    private val TAG = "Repository"

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://quizz-77a0e-default-rtdb.europe-west1.firebasedatabase.app")
    private val myRef: DatabaseReference = database.reference
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun register(email: String, password: String): Boolean{
        var isRegistrationSuccessful = false
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "registered successfully!")
                Log.d(TAG, myRef.toString())
                Log.d(TAG, myRef.child("61DyAlB773cnOXZOLC4D64QgzgE3").toString())
                try{
                    Log.d(TAG, getCurrentUser().uid.toString())
                    myRef.child(getCurrentUser().uid).child("highScore").setValue(0)
                } catch (e: Exception){
                    Log.d("WADAWD",e.message.toString())
                }
                isRegistrationSuccessful = true
            }
        }.addOnFailureListener {
            Log.d(TAG,it.message.toString())
            Toast.makeText(Quizz.context, it.message.toString(), Toast.LENGTH_SHORT).show()
        }.await()
        return isRegistrationSuccessful
    }

    suspend fun signIn(email: String, password: String): Boolean{

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
            }.await()
        }
        return signingSuccessful
    }

    fun signOut(){
        mAuth.signOut()
    }

    fun checkIfUserIsSignedIn() : Boolean {
        val currentUser : FirebaseUser? = mAuth.currentUser
        return currentUser != null
    }

    fun getCurrentUser(): FirebaseUser {
        return mAuth.currentUser!!
    }

    fun changePassword(email: String): Boolean{
        var isPasswordChangeRequested = false
        if(email == mAuth.currentUser?.email){
            mAuth.sendPasswordResetEmail(email)
            isPasswordChangeRequested = true
        }
        return isPasswordChangeRequested
    }

    fun setNewHighScore(highScore: Int){
        myRef.child(getCurrentUser().uid).child("highScore").setValue(highScore)
    }

    fun getHighScore(): Int{
        var highScore = 0
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    highScore = user?.highScore ?: 0
                    Log.d(TAG,highScore.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"retrieving highScore failed")
            }

        }
        myRef.addValueEventListener(listener)
        return highScore
    }

    private fun checkIfUsernameIsAvailable(username: String): Boolean{
        var isUsernameAvailable = true
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    Log.d(TAG,"checking username")
                    val user = it.getValue(User::class.java)
                    if(user?.username == username){
                        isUsernameAvailable = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"checking if username is available failed")
            }

        }
        myRef.addValueEventListener(listener)
        return isUsernameAvailable
    }

    fun chooseUsername(username: String): Boolean{
        return if(checkIfUsernameIsAvailable(username)){
            myRef.child(getCurrentUser().uid).child("username").setValue(username)
            true
        } else {
            Toast.makeText(Quizz.context,"Username unavailable!",Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun getCurrentUserObject(): User?{
        var user: User? = User()
        val listener = object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.child(getCurrentUser().uid).getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "checking if username is available failed")
            }
        }
        myRef.addValueEventListener(listener)
        return user
    }
}