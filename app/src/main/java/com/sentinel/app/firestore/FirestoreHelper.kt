package com.sentinel.app.firestore

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sentinel.app.models.ScanData
import com.sentinel.app.models.ScanResult
import com.sentinel.app.models.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirestoreHelper(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String?
        get() = auth.currentUser?.uid

    // Save scan result to Firestore
    suspend fun saveScanResult(scanResult: ScanResult): Result<String> {
        return try {
            if (userId == null) return Result.failure(Exception("User not authenticated"))

            val scanData = hashMapOf(
                "appName" to scanResult.appName,
                "packageName" to scanResult.packageName,
                "riskLevel" to scanResult.riskLevel,
                "riskScore" to scanResult.riskScore,
                "dangerousPermsCount" to scanResult.dangerousPermsCount,
                "scanDate" to scanResult.scanDate,
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )

            val docRef = db.collection("users")
                .document(userId!!)
                .collection("scans")
                .add(scanData)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving scan", e)
            Result.failure(e)
        }
    }

    // Real-time listener for scan results
    fun observeScanResults(): Flow<List<ScanResult>> = callbackFlow {
        if (userId == null) {
            close()
            return@callbackFlow
        }

        val registration: ListenerRegistration = db.collection("users")
            .document(userId!!)
            .collection("scans")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val scans = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        ScanResult(
                            appName = data["appName"] as? String ?: "",
                            packageName = data["packageName"] as? String ?: "",
                            riskLevel = data["riskLevel"] as? String ?: "",
                            scanDate = (data["scanDate"] as? Date) ?: Date(),
                            dangerousPermsCount = (data["dangerousPermsCount"] as? Long)?.toInt() ?: 0,
                            riskScore = (data["riskScore"] as? Long)?.toInt() ?: 0
                        )
                    } else null
                } ?: emptyList()

                trySend(scans)
            }

        awaitClose { registration.remove() }
    }

    // Save user profile
    suspend fun saveUserProfile(userName: String, deviceName: String): Result<Unit> {
        return try {
            if (userId == null) return Result.failure(Exception("User not authenticated"))

            val profile = hashMapOf(
                "userName" to userName,
                "deviceName" to deviceName,
                "email" to auth.currentUser?.email,
                "lastActive" to System.currentTimeMillis()
            )

            db.collection("users")
                .document(userId!!)
                .set(profile)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get user profile
    suspend fun getUserProfile(): UserProfile? {
        return try {
            if (userId == null) return null

            val document = db.collection("users")
                .document(userId!!)
                .get()
                .await()

            if (document.exists()) {
                UserProfile(
                    userId = userId!!,
                    userName = document.getString("userName") ?: "",
                    email = document.getString("email") ?: "",
                    deviceName = document.getString("deviceName") ?: "",
                    lastActive = Date(document.getLong("lastActive") ?: System.currentTimeMillis())
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Delete scan result
    suspend fun deleteScanResult(scanId: String): Result<Unit> {
        return try {
            if (userId == null) return Result.failure(Exception("User not authenticated"))

            db.collection("users")
                .document(userId!!)
                .collection("scans")
                .document(scanId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}