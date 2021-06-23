package com.example.quizz.ui.viewmodels

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.service.quickaccesswallet.QuickAccessWalletService
import android.util.Base64
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizz.data.model.User
import com.example.quizz.data.repository.Repository
import androidx.lifecycle.*
import com.example.quizz.Quizz
import com.example.quizz.R
import com.example.quizz.helpers.ImageParser
import com.example.quizz.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.dialog_profile_image.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.net.URI

class MainScreenViewModel(private val repository: Repository): ViewModel() {

    private val TAG = "MainScreenViewModel"
    private var _usersForRecyclerViewFetched: MutableLiveData<List<User>> = MutableLiveData()
    var usersForRecyclerViewFetched: LiveData<List<User>> = _usersForRecyclerViewFetched
    private var _topThreePlayers: MutableLiveData<List<User>> = MutableLiveData()
    var topThreePlayers: LiveData<List<User>> = _topThreePlayers
    private var _accountDeleted: MutableLiveData<Boolean> = MutableLiveData(false)
    var accountDeleted: LiveData<Boolean> = _accountDeleted
    private var _imageUpdated: MutableLiveData<Uri?> = MutableLiveData()
    var imageUpdated: LiveData<Uri?> = _imageUpdated
    private var _photoUploadStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    var photoUploadStarted: LiveData<Boolean> = _photoUploadStarted
    private var _user: MutableLiveData<User> = MutableLiveData()
    var user: LiveData<User> = _user

    fun getPlayersForRecyclerView(){
        viewModelScope.launch {
            _usersForRecyclerViewFetched.postValue(repository.getPlayersForRecyclerView())}
    }
    fun getTopThreePlayers(){
        viewModelScope.launch {
            _topThreePlayers.postValue(repository.getTopThreePlayers())
        }
    }
    fun getUserFromRoomDatabase(){
//        viewModelScope.launch {
//
//        }
//       repository.getUser().collect {
//           Log.d(TAG,it.username)
//           _user.postValue(it)
//       }
        _user.postValue(repository.getUser())
    }

    fun getCurrentUser(): FirebaseUser {
        return repository.getCurrentUser()
    }

    suspend fun getCurrentUserObject(): User?{
        return repository.getCurrentUserObject()
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

    suspend fun signOut(){
        repository.signOut()
    }

    fun takePhoto(activity: MainActivity) {
        viewModelScope.launch {
            val bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(activity)
            bottomSheetDialog.setContentView(R.layout.dialog_profile_image)
            bottomSheetDialog.take_photo.setOnClickListener {
                Toast.makeText(activity,"Take photo!",Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(Quizz.context.packageManager) != null) {
                    startActivityForResult(activity,intent, REQUEST_TAKE_PHOTO,null)
                } else {
                     Log.d(TAG,"Photo could not be taken!")
                }
            }
            bottomSheetDialog.choose_from_gallery.setOnClickListener {
                Toast.makeText(activity,"Choose from gallery!",Toast.LENGTH_SHORT).show()
                bottomSheetDialog.dismiss()
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                if (intent.resolveActivity(Quizz.context.packageManager) != null) {
                    startActivityForResult(activity,intent, REQUEST_SELECT_IMAGE_IN_ALBUM,null)
                }
            }
            bottomSheetDialog.show()
        }
    }

    suspend fun getPhoto(){
        viewModelScope.launch {
            val image = repository.getPhoto(getCurrentUserObject()?.username!!)
            _imageUpdated.postValue(image)
        }
    }

    fun updatePhoto(imageUri: Uri){
        viewModelScope.launch {
            _photoUploadStarted.postValue(true)
            repository.updatePhoto(imageUri,getCurrentUserObject()?.username!!)
            _photoUploadStarted.postValue(false)
            getPhoto()
        }
    }

    companion object {
        val REQUEST_TAKE_PHOTO = 0
        val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }
}