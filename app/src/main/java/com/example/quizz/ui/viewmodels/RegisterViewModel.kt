package com.example.quizz.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private var _isRegistrationPossible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isRegistrationPossible: LiveData<Boolean> = _isRegistrationPossible
    private var _isPasswordChanged: MutableLiveData<Boolean> = MutableLiveData(false)
    var isPasswordChanged: LiveData<Boolean> = _isPasswordChanged
    private var _isUserSignedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignedIn: LiveData<Boolean> = _isUserSignedIn

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance();

    //FirebaseAuth
    fun register(email: String, password: String){
        viewModelScope.launch {
            _isRegistrationPossible.postValue(true)
        }
    }

    fun changePassword(newPassword: String){
        //change password
        _isPasswordChanged.postValue(true)
    }

    fun checkIfUserIsSignedIn() {
        var currentUser :FirebaseUser? = mAuth.currentUser
        if(currentUser != null){
            _isUserSignedIn.postValue(true)
        }
    }

    fun getCurrentUser(): FirebaseUser{
        return mAuth.currentUser!!
    }

}