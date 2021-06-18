package com.example.quizz.ui.viewmodels

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class MainScreenViewModel(private val repository: Repository): ViewModel() {

    private val TAG = "MainScreenViewModel"
    private var _usersForRecyclerViewFetched: MutableLiveData<List<User>> = MutableLiveData()
    var usersForRecyclerViewFetched: LiveData<List<User>> = _usersForRecyclerViewFetched
    private var _topThreePlayers: MutableLiveData<List<User>> = MutableLiveData()
    var topThreePlayers: LiveData<List<User>> = _topThreePlayers
    private var _accountDeleted: MutableLiveData<Boolean> = MutableLiveData(false)
    var accountDeleted: LiveData<Boolean> = _accountDeleted
    private var _imageUpdated: MutableLiveData<Bitmap?> = MutableLiveData()
    var imageUpdated: LiveData<Bitmap?> = _imageUpdated
    private var _photoUploadStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    var photoUploadStarted: LiveData<Boolean> = _photoUploadStarted

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

    fun signOut(){
        repository.signOut()
    }

//
//    fun selectImageInAlbum(activity: MainActivity) {
//        viewModelScope.launch {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            if (intent.resolveActivity(Quizz.context.packageManager) != null) {
//                startActivityForResult(activity,intent, REQUEST_SELECT_IMAGE_IN_ALBUM,null)
//            }
//        }
//
//    }

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
            _imageUpdated.postValue(ImageParser.byteArrayToBitMap(image))
        }
    }

    fun updatePhoto(bitmap: Bitmap){
        viewModelScope.launch {
            _photoUploadStarted.postValue(true)
            repository.updatePhoto(bitmap,getCurrentUserObject()?.username!!)
            _photoUploadStarted.postValue(false)
            getPhoto()
        }
    }

    companion object {
        val REQUEST_TAKE_PHOTO = 0
        val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }
}