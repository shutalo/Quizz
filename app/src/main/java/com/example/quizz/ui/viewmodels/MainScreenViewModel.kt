package com.example.quizz.ui.viewmodels

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizz.data.model.User
import com.example.quizz.data.repository.Repository
import androidx.lifecycle.*
import com.example.quizz.Quizz
import com.example.quizz.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainScreenViewModel(private val repository: Repository): ViewModel() {

    private var _usersForRecyclerViewFetched: MutableLiveData<List<User>> = MutableLiveData()
    var usersForRecyclerViewFetched: LiveData<List<User>> = _usersForRecyclerViewFetched
    private var _topThreePlayers: MutableLiveData<List<User>> = MutableLiveData()
    var topThreePlayers: LiveData<List<User>> = _topThreePlayers
    private var _accountDeleted: MutableLiveData<Boolean> = MutableLiveData(false)
    var accountDeleted: LiveData<Boolean> = _accountDeleted
    private var _imageUpdated: MutableLiveData<Bitmap> = MutableLiveData()
    var imageUpdated: LiveData<Bitmap> = _imageUpdated

    fun getPlayersForRecyclerView(){
        viewModelScope.launch {
            _usersForRecyclerViewFetched.postValue(repository.getPlayersForRecyclerView())}
    }
    fun getTopThreePlayers(){
        viewModelScope.launch {
            _topThreePlayers.postValue(repository.getTopThreePlayers())
        }
    }

    fun getCurrentUser(): FirebaseUser {
        return repository.getCurrentUser()
    }

    fun updatePassword(newPassword: String, confirmPassword: String){
        viewModelScope.launch {
            repository.updatePassword(newPassword,confirmPassword)
        }
    }

    fun deleteAccount(){
        viewModelScope.launch {
            _accountDeleted.postValue(repository.deleteAccount())
        }
    }

    fun signOut(){
        repository.signOut()
    }

    fun selectImageInAlbum(activity: MainActivity) {
        viewModelScope.launch {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(Quizz.context.packageManager) != null) {
                startActivityForResult(activity,intent, REQUEST_SELECT_IMAGE_IN_ALBUM,null)
            }
        }

    }
    fun takePhoto(activity: MainActivity) {
        viewModelScope.launch {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(Quizz.context.packageManager) != null) {
                startActivityForResult(activity,intent, REQUEST_TAKE_PHOTO,null)
            }
        }
    }

    suspend fun getPhoto(){
    }

    fun updatePhoto(bitmap: Bitmap){
        viewModelScope.launch {
            repository.updatePhoto(bitmap)
            _imageUpdated.postValue(repository.getPhoto())
        }
    }

    companion object {
        val REQUEST_TAKE_PHOTO = 0
        val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }
}