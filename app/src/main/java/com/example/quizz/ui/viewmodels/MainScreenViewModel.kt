package com.example.quizz.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizz.data.model.User
import com.example.quizz.data.repository.Repository
import androidx.lifecycle.*
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: Repository): ViewModel() {

    private var _usersForRecyclerViewFetched: MutableLiveData<List<User>> = MutableLiveData()
    var usersForRecyclerViewFetched: LiveData<List<User>> = _usersForRecyclerViewFetched
    private var _topThreePlayers: MutableLiveData<List<User>> = MutableLiveData()
    var topThreePlayers: LiveData<List<User>> = _topThreePlayers

    fun getPlayersForRecyclerView(){
        viewModelScope.launch {
            _usersForRecyclerViewFetched.postValue(repository.getPlayersForRecyclerView())}
    }
    fun getTopThreePlayers(){
        viewModelScope.launch {
            _topThreePlayers.postValue(repository.getTopThreePlayers())
        }
    }
}