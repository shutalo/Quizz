package com.example.quizz.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.data.model.User
import com.example.quizz.data.room.Dao
import com.example.quizz.helpers.ImageParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.concurrent.Flow
import kotlin.collections.HashMap

class Repository(private val dao: Dao) {

    private val TAG = "Repository"

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://quizz-77a0e.appspot.com")


    fun getUser(): kotlinx.coroutines.flow.Flow<User> = flow<User>{
        CoroutineScope(Dispatchers.IO).launch {
            emit(dao.getUser())
        }
    }
    fun delete(user: User) = dao.deleteUser(user)

    suspend fun register(email: String, password: String): Boolean{
        var isRegistrationSuccessful = false
        try {
            mAuth.createUserWithEmailAndPassword(email,password).await()
            val highScore = HashMap<String,Int>()
            highScore["highScore"] = 0
            database.collection("users").document(getCurrentUser().uid).set(highScore)
            Log.d(TAG,"User added to database")
            isRegistrationSuccessful = true
            Toast.makeText(Quizz.context, "Registered successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception){
            Log.d(TAG,e.message.toString())
        }
        return isRegistrationSuccessful
    }

    suspend fun signIn(email: String, password: String): Boolean{
        var signingSuccessful: Boolean = false
        if(email != "" && password != ""){
            try {
                mAuth.signInWithEmailAndPassword(email, password).await()
                signingSuccessful = true
                putUserToRoomDatabase()
            } catch (e: Exception){
                Log.d(TAG,e.message.toString())
            }
        }
        return signingSuccessful
    }

    suspend fun signOut(){
        dao.deleteUser(getCurrentUserObject()!!)
        mAuth.signOut()
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
            database.collection("users").document(getCurrentUser().uid).update("username",username).await()
            putUserToRoomDatabase()
            //updateInitialPhoto(username)
            true
        } else {
            Toast.makeText(Quizz.context,"Username unavailable!",Toast.LENGTH_SHORT).show()
            false
        }
    }

    private suspend fun putUserToRoomDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(database.collection("users").document(getCurrentUser().uid).get().await().toObject(User::class.java)!!)
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
        if(users.size >= 3){
            for(i in 0..2){
                users.removeAt(0)
            }
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
            users[i].photo = getPhoto(users[i].username!!)
            topThreePlayers.add(users[i])
        }
        return topThreePlayers
    }

    suspend fun updatePassword(newPassword: String, confirmPassword: String){
        if(newPassword == "" || confirmPassword == ""){
            Toast.makeText(Quizz.context,"Password entry must not be empty!", Toast.LENGTH_SHORT).show()
        } else if(newPassword == confirmPassword && newPassword.length < 6){
            Toast.makeText(Quizz.context,"Password too short!", Toast.LENGTH_SHORT).show()
        } else if(newPassword != confirmPassword){
            Toast.makeText(Quizz.context,"Password must match!", Toast.LENGTH_SHORT).show()
        } else if(newPassword == confirmPassword){
            getCurrentUser().updatePassword(newPassword).await()
            Toast.makeText(Quizz.context,"Password changed successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun deleteAccount(): Boolean{
        try {
            storage.reference.child("images/${getCurrentUserObject()?.username}/${getCurrentUserObject()?.username}.jpg").delete().await()
            database.collection("users").document(getCurrentUser().uid).delete().await()
        } catch (e: Exception){
            Log.d(TAG,e.message.toString())
        }
        getCurrentUser().delete().await()
        Toast.makeText(Quizz.context,"Account deleted.",Toast.LENGTH_SHORT).show()
        return true
    }

    suspend fun getPhoto(username: String): Uri? {
        return try {
            val storageReference = storage.reference.child("images/$username/$username.jpg")
            storageReference.downloadUrl.await()
        } catch (e: Exception){
            Log.d(TAG,e.message.toString())
            null
        }
    }

    suspend fun updatePhoto(imageUri: Uri, username: String){
        val storageReference = storage.reference.child("images/$username/$username.jpg")
        storageReference.putFile(imageUri).await()
    }

    private suspend fun updateInitialPhoto(username: String){
        val storageReference = storage.reference.child("images/$username/$username.jpg")
        storageReference.putBytes(ImageParser.bitMapToByteArray(BitmapFactory.decodeResource(Quizz.context.resources,R.drawable.profile_photo))).await()
    }

    suspend fun getHighScore(): Int{
        val user = database.collection("users").document(getCurrentUser().uid).get().await().toObject(User::class.java)
        return user?.highScore!!
    }

    fun updateHighScore(score: Int){
        val highScore = HashMap<String,Int>()
        highScore["highScore"] = score
        database.collection("users").document(getCurrentUser().uid).set(highScore)
    }
}