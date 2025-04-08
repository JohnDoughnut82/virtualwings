package com.example.virtualwing.repository

import android.util.Log
import com.example.virtualwing.data.FlightLog
import com.example.virtualwing.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    suspend fun getUserNameFromProfile(userId: String): String? {
        return try{
            val userProfile = getUserProfile(userId)
            userProfile?.name
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching user name", e)
            null
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val documentSnapshot = firestore.collection("users").document(userId).get().await()
            documentSnapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetch user profile", e)
            null
        }
    }

    suspend fun updateUserProfile(userId: String, updatedProfile: UserProfile): Boolean {
        return try {
            firestore.collection("users").document(userId).set(updatedProfile).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating profile", e)
            false
        }
    }

    suspend fun saveFlightLog(userId: String, flightLog: FlightLog): Boolean {
        return try {
            firestore.collection("users").document(userId)
                .collection("flightLogs").add(flightLog).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error saving flight log", e)
            false
        }
    }

    suspend fun getUserFlightHours(userId: String): Int {
        return try {
            val querySnapshot = firestore.collection("users").document(userId)
                .collection("flightLogs").get().await()

            var totalFlightHours = 0
            for (document in querySnapshot) {
                val flightLog = document.toObject(FlightLog::class.java)
                totalFlightHours += flightLog.flightHours
            }
            totalFlightHours
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching flight hours", e)
            0
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Login failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUpUser(
        email: String,
        password: String,
        userProfile: UserProfile
    ): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("User ID is null"))

            firestore.collection("users").document(uid).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign up failed", e)
            Result.failure(e)
        }
    }
}