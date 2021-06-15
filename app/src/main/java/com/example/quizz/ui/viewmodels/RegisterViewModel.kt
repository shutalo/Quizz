package com.example.quizz.ui.viewmodels

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.example.quizz.Quizz
import com.example.quizz.data.model.User
import com.example.quizz.data.repository.Repository
import com.example.quizz.ui.activities.WelcomeActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val TAG = "RegisterViewModel"

    private var _isUserRegisteredSuccessfully: MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserRegisteredSuccessfully: LiveData<Boolean> = _isUserRegisteredSuccessfully
    private var _isPasswordChangeRequested: MutableLiveData<Boolean> = MutableLiveData(false)
    var isPasswordChangeRequested: LiveData<Boolean> = _isPasswordChangeRequested
    private var _isUserSignedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignedIn: LiveData<Boolean> = _isUserSignedIn
    private var _isSigningInSuccessful: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSigningInSuccessful: LiveData<Boolean> = _isSigningInSuccessful
    private var _isUsernameChosen: MutableLiveData<Boolean> = MutableLiveData(false)
    var isUsernameChosen: LiveData<Boolean> = _isUsernameChosen


    fun register(email: String, password: String){
        viewModelScope.launch {
            if(email != "" && password != "" && email.contains("@") && email.endsWith(".com") && !email.endsWith("@.com")){
                _isUserRegisteredSuccessfully.postValue(repository.register(email,password))
            } else {
                if(password.length < 6){
                    Toast.makeText(Quizz.context,"Password must contain more than 6 characters!",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(Quizz.context,"Wrong format.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun signIn(email: String, password: String){
        viewModelScope.launch {
           _isSigningInSuccessful.postValue(repository.signIn(email,password))
        }
    }

    fun changePassword(email: String){
        viewModelScope.launch {
            repository.changePassword(email)
            _isPasswordChangeRequested.postValue(true)
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

    suspend fun getCurrentUserObject(): User?{
        return repository.getCurrentUserObject()
    }

    fun chooseUsername(username: String){
        viewModelScope.launch {
            _isUsernameChosen.postValue(repository.chooseUsername(username))
        }
    }

}