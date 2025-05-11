package com.example.virtualwing.repository

import android.net.Uri
import com.example.virtualwing.data.Squadron
import com.google.firebase.firestore.FirebaseFirestore
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
            "members" to listOf(creatorId),
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("squadrons")
            .document(squadronId)
            .set(squadronData)
            .await()
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
    }


}

