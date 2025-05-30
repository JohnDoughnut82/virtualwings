package com.example.virtualwing.viewmodel.squadron

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.repository.SquadronRepository
import com.google.firebase.auth.FirebaseAuth
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

    private val _joinRequestResult = MutableLiveData<Result<Unit>>()
    val joinRequestResult: LiveData<Result<Unit>> = _joinRequestResult

    private val _joinRequests = MutableLiveData<List<UserProfile>>()
    val joinRequests: LiveData<List<UserProfile>> get() = _joinRequests

    private val _squadronMembers = MutableLiveData<List<UserProfile>>()
    val squadronMembers: LiveData<List<UserProfile>> get() = _squadronMembers

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    fun checkUserSquadron(userId: String) {
        viewModelScope.launch {
            val updatedSquadron = squadronRepository.getUserSquadron(userId)
            _userSquadron.postValue(updatedSquadron)
        }
    }

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val profile = squadronRepository.getUserProfileById(userId)
                _userProfile.postValue(profile)
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Error fetching profile", e)
                _userProfile.postValue(null)
            }
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

    fun getAllSquadrons(): LiveData<List<Squadron>> {
        val result = MutableLiveData<List<Squadron>>()
        viewModelScope.launch {
            val list = squadronRepository.getAllSquadrons()
            result.postValue(list)
        }
        return result
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

    fun sendJoinRequest(squadronId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                squadronRepository.sendJoinRequest(userId, squadronId)
                _joinRequestResult.postValue(Result.success(Unit))
            } catch (e: Exception) {
                _joinRequestResult.postValue(Result.failure(e))
            }
        }
    }

    fun getPendingJoinRequests(squadronId: String) {
        viewModelScope.launch {
            try {
                val requests = squadronRepository.getJoinRequestsForSquadron(squadronId)
                _joinRequests.value = requests
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Error loading join requests", e)
                _joinRequests.value = emptyList()
            }
        }
    }

    fun approveJoinRequest(userId: String, squadronId: String) {
        viewModelScope.launch {
            try {
                squadronRepository.approveUserJoinRequest(userId, squadronId)
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Approval failed", e)
            }
        }
    }

    fun denyJoinRequest(userId: String, squadronId: String) {
        viewModelScope.launch {
            try {
                squadronRepository.denyUserJoinRequest(userId, squadronId)
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Denial failed", e)
            }
        }
    }

    fun loadSquadronMembers() {
        viewModelScope.launch {
            try {
                val squadron = _userSquadron.value
                if (squadron == null) {
                    Log.e("SquadronViewModel", "Squadron not loaded.")
                    return@launch
                }

                val freshSquadron = squadronRepository.getUserSquadron(squadron.creatorId)
                val memberIds = freshSquadron?.members ?: emptyList()

                val memberProfiles = memberIds.mapNotNull { id ->
                    try {
                        squadronRepository.getUserProfileById(id)
                    } catch (e: Exception) {
                        null
                    }
                }

                _squadronMembers.postValue(memberProfiles)
                _userSquadron.postValue(freshSquadron)
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Failed to load squadron members", e)
            }
        }
    }

    fun promoteToAdmin(userId: String, squadronId: String) {
        viewModelScope.launch {
            try {
                squadronRepository.promoteToAdmin(userId, squadronId)
                loadSquadronMembers()
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Failed to promote member", e)
            }
        }
    }

    fun removeMember(userId: String, squadronId: String) {
        viewModelScope.launch {
            try {
                squadronRepository.removeMember(userId, squadronId)
                loadSquadronMembers()
            } catch (e: Exception) {
                Log.e("SquadronViewModel", "Failed to remove member", e)
            }
        }
    }
}
