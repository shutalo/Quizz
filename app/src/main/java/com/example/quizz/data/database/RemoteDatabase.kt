package com.example.quizz.data.database

import android.util.Log
import com.example.quizz.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class RemoteDatabase {

    private val TAG = "RemoteDatabase"

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.getReference("users")

    fun setNewHighScore(highScore: Int, currentUser: FirebaseUser){
        myRef.child(currentUser.uid).child("highScore").setValue(highScore)
    }

    fun getHighScore(): Int{
        var highScore = 0
        val listener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val user = it.getValue(User::class.java)
                    highScore = user?.highScore ?: 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"retrieving highScore failed")
            }

        }
        myRef.addValueEventListener(listener)
        return highScore
    }

    fun checkIfUsernameIsAvailable(username: String): Boolean{
        var isUsernameAvailable = true
        val listener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
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

    fun chooseUsername(username: String,currentUser: FirebaseUser){
        myRef.child(currentUser.uid).child("username").setValue(username)
    }

    fun getCurrentUserObject(currentUser: FirebaseUser): User?{
        var user: User? = User()
        val listener = object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.child(currentUser.uid).getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "checking if username is available failed")
            }
        }
        myRef.addValueEventListener(listener)
        return user
    }
}
