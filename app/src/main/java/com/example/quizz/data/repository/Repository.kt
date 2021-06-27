package com.example.quizz.data.repository

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.data.model.Question
import com.example.quizz.data.model.Token
import com.example.quizz.data.model.User
import com.example.quizz.data.room.Dao
import com.example.quizz.data.room.QuestionsDao
import com.example.quizz.data.room.TokenDao
import com.example.quizz.helpers.ImageParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.koin.core.component.getScopeId
import kotlin.collections.HashMap

class Repository(private val dao: Dao,private val tokenDao: TokenDao) {

    private val TAG = "Repository"

    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://quizz-77a0e.appspot.com")

    fun getUserFromRoomDatabase(): User {
        return dao.getUser()
    }
    fun delete(user: User) = dao.deleteUser(user)
    private suspend fun putUserToRoomDatabase() {
        dao.insert(getCurrentUserObject()!!)
    }

    suspend fun register(email: String, password: String): Boolean{
        var isRegistrationSuccessful = false
        try {
            mAuth.createUserWithEmailAndPassword(email,password).await()
            val highScore = HashMap<String,Int>()
            highScore["highScore"] = 0
            database.collection("users").document(getCurrentUser().uid).set(highScore)
            Log.d(TAG + "register","User added to database")
            isRegistrationSuccessful = true
            Toast.makeText(Quizz.context, "Registered successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception){
            Log.d(TAG + "register",e.message.toString())
            Toast.makeText(Quizz.context,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
        return isRegistrationSuccessful
    }

    suspend fun signIn(email: String, password: String): Boolean{
        var signingSuccessful: Boolean = false
        try {
            mAuth.signInWithEmailAndPassword(email, password).await()
            signingSuccessful = true
            putUserToRoomDatabase()
        } catch (e: Exception){
            Log.d(TAG + "sign in",e.message.toString())
            Toast.makeText(Quizz.context,"Wrong username or password",Toast.LENGTH_SHORT).show()
        }
        return signingSuccessful
    }

    suspend fun signOut(){
        dao.deleteUser(getCurrentUserObject())
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
            true
        } else {
            Toast.makeText(Quizz.context,"Username unavailable!",Toast.LENGTH_SHORT).show()
            false
        }
    }

    suspend fun getCurrentUserObject(): User {
        val user = database.collection("users").document(getCurrentUser().uid).get().await()
        return User(
            username = user.getField<String>("username")!!,
            highScore = user.getField<Int>("highScore")!!,
            photo = getPhoto(user.getField<String>("username")!!)
        )
    }

    suspend fun getPlayersForRecyclerView(): List<User>{
        val users: MutableList<User> = mutableListOf()
        val players = database.collection("users").get().await()
        players.forEach {
                val user = User(it.getField<String>("username")!!,it.getField<Int>("highScore")!!)
            users.add(user)
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
            val user = User(it.getField<String>("username")!!,it.getField<Int>("highScore")!!)
            users.add(user)
        }
        users.sortByDescending {
            it.highScore
        }
        val topThreePlayers = mutableListOf<User>()
        for(i in 0..2){
            users[i].photo = getPhoto(users[i].username)
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
            if(checkIfImageExistsInFirebaseStorage(getCurrentUserObject().username)){
                delete(getCurrentUserObject())
                storage.reference.child("images/${getCurrentUserObject().username}/${getCurrentUserObject().username}.jpg").delete().await()
            }
            database.collection("users").document(getCurrentUser().uid).delete().await()
        } catch (e: Exception){
            Log.d(TAG + "delete acc",e.message.toString())
        }
        getCurrentUser().delete().await()
        Toast.makeText(Quizz.context,"Account deleted.",Toast.LENGTH_SHORT).show()
        return true
    }

    suspend fun getPhoto(username: String): Uri? {
        return try {
            if(checkIfImageExistsInFirebaseStorage(username)){
                val storageReference = storage.reference.child("images/${username}/${username}.jpg")
                return storageReference.downloadUrl.await()
            }
            null
        } catch (e: Exception){
            Log.d(TAG + "getPhoto",e.message.toString())
            null
        }
    }

    private suspend fun checkIfImageExistsInFirebaseStorage(username: String): Boolean{
        val users = mutableListOf<String>()
        val storageReference = storage.reference
        storageReference.root.child("images").listAll().await().also { it1 ->
            it1.prefixes.forEach {
                users.add(it.toString().substringAfter("images/"))
            }
        }
        users.forEach {
            if(it == username) {
                return true
            }
        }
        return false
    }

    suspend fun updatePhoto(imageUri: Uri, username: String){
        val storageReference = storage.reference.child("images/$username/$username.jpg")
        storageReference.putFile(imageUri).await()
        dao.insert(getCurrentUserObject())
    }

    fun getHighScore(): Int{
        return dao.getUser().highScore
    }

    suspend fun updateHighScore(score: Int){
        database.collection("users").document(getCurrentUser().uid).update("highScore",score).await()
        dao.insert(getCurrentUserObject())
    }

    fun getTokenFromRoomDatabase(): Token?{
        return tokenDao.getToken()
    }

    fun saveTokenToRoomDatabase(token: Token){
        tokenDao.insert(token)
    }
//
//    fun saveQuestionsToRoomDatabase(questions: List<Question>){
//        questions.forEach{
//            questionsDao.insert(it)
//        }
//    }
//
//    fun getQuestionsFromRoomDatabase(): List<Question>{
//        return questionsDao.getQuestions()
//    }
//
//    fun clearQuestionsDatabase(){
//        questionsDao.deleteAll()
//    }
}