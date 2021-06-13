package com.example.quizz.data.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RemoteDatabase {

    private val TAG = "RemoteDatabase"

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = database.reference

    fun setNewHighScore(){

    }

//    fun getHighScore(): Int{
//        myRef.addValueEventListener{}
//    }

    fun checkIfUsernameIsAvailable(){
        //check if selected username is available and add it to database
    }
}
