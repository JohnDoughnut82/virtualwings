package com.example.virtualwing.repository

import android.net.Uri
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.data.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class SquadronService {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadSquadron(
        name: String,
        description: String,
        region: String,
        timezone: String,
        emblemUri: Uri?,
        creatorId: String
    ) {
        val userRef = firestore.collection("users").document(creatorId)
        val userSnapshot = userRef.get().await()
        val userProfile = userSnapshot.toObject(UserProfile::class.java)

        if (userProfile?.squadronId != null) {
            throw IllegalStateException("User is already a member or owner of a squadron.")
        }

        val squadronId = UUID.randomUUID().toString()

        var imageUrl: String? = null
        if(emblemUri != null) {
            val imageRef = storage.reference.child("squadrons/$squadronId/emblem.jpg")
            imageRef.putFile(emblemUri).await()
            imageUrl = imageRef.downloadUrl.await().toString()
        }

        val squadronData = mapOf(
            "id" to squadronId,
            "name" to name,
            "description" to description,
            "region" to region,
            "timezone" to timezone,
            "emblemUrl" to imageUrl,
            "creatorId" to creatorId,
            "createdBy" to userProfile?.name.orEmpty(),
            "members" to listOf(creatorId),
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("squadrons")
            .document(squadronId)
            .set(squadronData)
            .await()

        userRef.update(
            mapOf(
                "squadronId" to squadronId,
                "isSquadronCreator" to true
            )
        ).await()
    }

    suspend fun fetchUserProfileById(userId: String): UserProfile? {
        val doc = firestore.collection("users").document(userId).get().await()
        return doc.toObject(UserProfile::class.java)?.copy(id = doc.id)
    }

    suspend fun fetchAllSquadrons(): List<Squadron> {
        val querySnapshot = firestore.collection("squadrons").get().await()
        return querySnapshot.documents.mapNotNull { it.toObject(Squadron::class.java) }
    }

    suspend fun fetchSquadronByUserId(userId: String): Squadron? {
        val query = firestore.collection("squadrons")
            .whereArrayContains("members", userId)
            .get()
            .await()

        val doc = query.documents.firstOrNull() ?: return null
        return doc.toObject(Squadron::class.java)
    }

    suspend fun leaveSquadron(userId: String, squadronId: String) {
        val squadRef = firestore.collection("squadrons").document(squadronId)
        val snapshot = squadRef.get().await()

        val currentMembers = snapshot.get("members") as? List<String> ?: return
        val updatedMembers = currentMembers.filterNot { it == userId }

        squadRef.update("members", updatedMembers).await()

        firestore.collection("users")
            .document(userId)
            .update(
                mapOf(
                    "squadronId" to null,
                    "isSquadronCreator" to false
                )
            ).await()
    }

    suspend fun disbandSquadron(userId: String, squadronId: String) {
        val squadRef = firestore.collection("squadrons").document(squadronId)
        val snapshot = squadRef.get().await()

        if (snapshot.getString("creatorId") != userId) throw IllegalAccessException("Only creator can disband.")

        try {
            val emblemRef = storage.reference.child("squadrons/$squadronId/emblem.jpg")
            emblemRef.delete().await()
        } catch (_: Exception) { /* Silent if emblem doesn't exist */ }

        squadRef.delete().await()

        firestore.collection("users")
            .document(userId)
            .update(
                mapOf(
                    "squadronId" to null,
                    "isSquadronCreator" to false
                )
            ).await()
    }

    suspend fun sendJoinRequest(userId: String, squadronId: String) {
        val userRef = firestore.collection("users").document(userId)
        val userSnapshot = userRef.get().await()
        val user = userSnapshot.toObject(UserProfile::class.java)
            ?: throw IllegalStateException("User profile not found.")

        if (user.squadronId != null) {
            throw IllegalStateException("You're already in a squadron.")
        }

        val joinRequestsRef = firestore.collection("squadrons")
            .document(squadronId)
            .collection("joinRequests")

        val existingRequest = joinRequestsRef.document(userId).get().await()
        if (existingRequest.exists()) {
            throw IllegalStateException("Join request already pending.")
        }

        val requestData = mapOf(
            "userId" to userId,
            "name" to user.name,
            "totalFlightHours" to user.totalFlightHours,
            "timestamp" to System.currentTimeMillis()
        )

        joinRequestsRef.document(userId).set(requestData).await()
    }

    suspend fun getJoinRequestsForSquadron(squadronId: String): List<UserProfile> {
        val joinRequestRef = firestore.collection("squadrons")
            .document(squadronId)
            .collection("joinRequests")

        val snapshot = joinRequestRef.get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(UserProfile::class.java)?.copy(id = doc.id)
        }
    }

    suspend fun approveUserJoinRequest(userId: String, squadronId: String) {
        val squadronRef = firestore.collection("squadrons").document(squadronId)
        val userRef = firestore.collection("users").document(userId)
        val joinRequestRef = squadronRef.collection("joinRequests").document(userId)

        firestore.runBatch { batch ->
            batch.update(squadronRef, "members", FieldValue.arrayUnion(userId))
            batch.update(userRef, mapOf(
                "squadronId" to squadronId,
                "isSquadronCreator" to false
            ))
            batch.delete(joinRequestRef)
        }.await()
    }

    suspend fun denyUserJoinRequest(userId: String, squadronId: String) {
        val requestRef = firestore.collection("squadrons")
            .document(squadronId)
            .collection("joinRequests")
            .document(userId)

        requestRef.delete().await()
    }

    suspend fun promoteUserToAdmin(userId: String, squadronId: String) {
        val userRef = firestore.collection("users").document(userId)
        val userSnapshot = userRef.get().await()

        val userProfile = userSnapshot.toObject(UserProfile::class.java)
            ?: throw IllegalArgumentException("User not found")

        if (userProfile.squadronId != squadronId) {
            throw IllegalStateException("User is not a member of this squadron.")
        }

        userRef.update("isSquadronAdmin", true).await()
    }

    suspend fun removeMemberFromSquadron(userId: String, squadronId: String) {
        val squadronRef = firestore.collection("squadrons").document(squadronId)
        val userRef = firestore.collection("users").document(userId)

        firestore.runBatch { batch ->
            batch.update(squadronRef, "members", FieldValue.arrayRemove(userId))
            batch.update(userRef, mapOf(
                "squadronId" to null,
                "isSquadronAdmin" to false,
                "isSquadronCreator" to false
            ))
        }.await()
    }
}

