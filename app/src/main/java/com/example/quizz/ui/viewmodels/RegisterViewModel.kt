package com.example.quizz.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.data.repository.Repository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "RegisterViewModel"

    private var _isRegistrationSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    var isRegistrationSuccessful: LiveData<Boolean> = _isRegistrationSuccessful
    private var _isPasswordChanged: MutableLiveData<Boolean> = MutableLiveData(false)
    var isPasswordChanged: LiveData<Boolean> = _isPasswordChanged
    private var _isUserSignedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignedIn: LiveData<Boolean> = _isUserSignedIn
    private var _isSigningInSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSigningInSuccessful: LiveData<Boolean> = _isSigningInSuccessful


    fun register(email: String, password: String){
        viewModelScope.launch {
           _isRegistrationSuccessful.postValue(repository.register(email,password))
        }
    }

    fun changePassword(newPassword: String){
        //change password
        _isPasswordChanged.postValue(true)
    }

    fun signIn(email: String, password: String){
        viewModelScope.launch {
            _isSigningInSuccessful.postValue(repository.signIn(email,password))
        }
    }

    fun checkIfUserIsSignedIn() {
        viewModelScope.launch {
            _isUserSignedIn.postValue(repository.checkIfUserIsSignedIn())
        }
    }

    fun getCurrentUser(): FirebaseUser{
        return repository.getCurrentUser()
    }

    fun chooseUsername(){
        //check if selected username is available and add it to database
    }

}