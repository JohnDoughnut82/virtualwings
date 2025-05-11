package com.example.virtualwing.repository

import android.net.Uri
import com.example.virtualwing.data.Squadron

class SquadronRepository(private val squadronService: SquadronService) {

    suspend fun getUserSquadron(userId: String): Squadron? {
        return squadronService.fetchSquadronByUserId(userId)
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
}