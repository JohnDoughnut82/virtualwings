package com.example.virtualwing.viewmodel.squadron

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.repository.SquadronRepository
import kotlinx.coroutines.launch

class SquadronViewModel(private val squadronRepository: SquadronRepository) : ViewModel() {

    private val _userSquadron = MutableLiveData<Squadron?>()
    val userSquadron: LiveData<Squadron?> = _userSquadron

    private val _creationResult = MutableLiveData<Result<Unit>>()
    val creationResult: LiveData<Result<Unit>> = _creationResult

    private val _leaveResult = MutableLiveData<Result<Unit>>()
    val leaveResult: LiveData<Result<Unit>> = _leaveResult

    private val _disbandResult = MutableLiveData<Result<Unit>>()
    val disbandResult: LiveData<Result<Unit>> = _disbandResult

    fun checkUserSquadron(userId: String) {
        viewModelScope.launch {
            _userSquadron.postValue(squadronRepository.getUserSquadron(userId))
        }
    }

    fun createSquadron(
        name: String,
        description: String,
        region: String,
        timezone: String,
        emblemUri: Uri?,
        creatorId: String
    ) {
        viewModelScope.launch {
            val result = squadronRepository.createSquadron(
                name, description, region, timezone, emblemUri, creatorId
            )
            _creationResult.postValue(result)
            if (result.isSuccess) checkUserSquadron(creatorId)
        }
    }

    fun leaveSquadron(userId: String, squadronId: String) {
        viewModelScope.launch {
            val result = squadronRepository.leaveSquadron(userId, squadronId)
            _leaveResult.postValue(result)
            if (result.isSuccess) _userSquadron.postValue(null)
        }
    }

    fun disbandSquadron(userId: String, squadronId: String) {
        viewModelScope.launch {
            val result = squadronRepository.disbandSquadron(userId, squadronId)
            _disbandResult.postValue(result)
            if (result.isSuccess) _userSquadron.postValue(null)
        }
    }
}