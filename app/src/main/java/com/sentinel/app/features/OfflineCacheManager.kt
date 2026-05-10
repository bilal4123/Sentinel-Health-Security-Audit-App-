package com.sentinel.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sentinel.app.models.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineCacheManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sentinel_cache",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()

    companion object {
        private const val KEY_RECENT_SCANS = "recent_scans"
        private const val KEY_THREAT_CACHE = "threat_cache"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val CACHE_DURATION_MS = 3600000 // 1 hour
    }

    // Cache scan results
    suspend fun cacheScanResults(scans: List<ScanResult>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(scans)
        prefs.edit().putString(KEY_RECENT_SCANS, json).apply()
        prefs.edit().putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis()).apply()
    }

    // Get cached scan results
    suspend fun getCachedScanResults(): List<ScanResult> = withContext(Dispatchers.IO) {
        val json = prefs.getString(KEY_RECENT_SCANS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<ScanResult>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Check if cache is valid
    fun isCacheValid(): Boolean {
        val lastCache = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        return System.currentTimeMillis() - lastCache < CACHE_DURATION_MS
    }

    // Clear all cache
    fun clearCache() {
        prefs.edit().clear().apply()
    }
}