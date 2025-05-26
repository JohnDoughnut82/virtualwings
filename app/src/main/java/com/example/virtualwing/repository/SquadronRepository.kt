package com.example.virtualwing.repository

import android.net.Uri
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.data.UserProfile
import com.google.firebase.firestore.auth.User

class SquadronRepository(private val squadronService: SquadronService) {

    suspend fun getUserSquadron(userId: String): Squadron? {
        return squadronService.fetchSquadronByUserId(userId)
    }

    suspend fun getAllSquadrons(): List<Squadron> {
        return squadronService.fetchAllSquadrons()
    }

    suspend fun getUserProfileById(userId: String): UserProfile? {
        return squadronService.fetchUserProfileById(userId)
    }

    suspend fun leaveSquadron(userId: String, squadronId: String): Result<Unit> = try {
        squadronService.leaveSquadron(userId, squadronId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun disbandSquadron(userId: String, squadronId: String): Result<Unit> = try {
        squadronService.disbandSquadron(userId, squadronId)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createSquadron(
        name: String,
        description: String,
        region: String,
        timezone: String,
        emblemUri: Uri?,
        creatorId: String
        ): Result<Unit> = try {
            squadronService.uploadSquadron(name, description, region, timezone, emblemUri, creatorId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun sendJoinRequest(userId: String, squadronId: String) {
        squadronService.sendJoinRequest(userId, squadronId)
    }

    suspend fun getJoinRequestsForSquadron(squadronId: String): List<UserProfile> {
       return squadronService.getJoinRequestsForSquadron(squadronId)
    }

    suspend fun approveUserJoinRequest(userId: String, squadronId: String) {
        return squadronService.approveUserJoinRequest(userId, squadronId)
    }


    suspend fun denyUserJoinRequest(userId: String, squadronId: String) {
        return squadronService.denyUserJoinRequest(userId, squadronId)
    }

    suspend fun promoteToAdmin(userId: String, squadronId: String) {
        squadronService.promoteUserToAdmin(userId, squadronId)
    }

    suspend fun removeMember(userId: String, squadronId: String) {
        squadronService.removeMemberFromSquadron(userId, squadronId)
    }
}