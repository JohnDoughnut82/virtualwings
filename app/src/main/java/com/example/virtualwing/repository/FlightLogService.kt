package com.example.virtualwing.repository

import android.util.Log
import com.example.virtualwing.data.FlightLog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FlightLogService {

    private val firestore = FirebaseFirestore.getInstance()
    

    suspend fun saveFlightLog(userId: String, flightLog: FlightLog): Boolean {
        return try{
            firestore.collection("users")
                .document(userId)
                .collection("flightLogs")
                .add(flightLog)
                .await()

            val userDocRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userDocRef)
                val currentTotal = snapshot.getLong("totalFlightHours") ?: 0
                val newTotal = currentTotal + flightLog.flightHours
                transaction.update(userDocRef, "totalFlightHours", newTotal)
            }.await()

            true
        } catch (e: Exception) {
            Log.e("FlightLogService", "Error saving flight log", e)
            false
        }
    }

    suspend fun getFlightLogs(userId: String): List<FlightLog> {
        return try {
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("flightLogs")
                .get()
                .await()

            querySnapshot.documents.mapNotNull {
                it.toObject(FlightLog::class.java)
            }
        } catch (e: Exception) {
            Log.e("FlightLogService", "Error fetching flight logs", e)
            emptyList()
        }
    }

    suspend fun getTotalFlightHours(userId: String): Int {
        return try {
            val querySnapshot = firestore.collection("users")
                .document(userId)
                .collection("flightLogs")
                .get()
                .await()

            var totalFlightHours = 0
            for (document in querySnapshot) {
                val flightLog = document.toObject(FlightLog::class.java)
                totalFlightHours += flightLog.flightHours
            }
            totalFlightHours
        } catch (e: Exception) {
            Log.e("FlightLogService", "Error fetching flight hours", e)
            0
        }
    }
}