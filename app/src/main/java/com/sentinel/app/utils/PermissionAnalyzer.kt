package com.sentinel.app.utils

import android.content.Context
import com.sentinel.app.models.Permission

class PermissionAnalyzer(private val context: Context) {

    fun calculateRiskScore(permissions: List<Permission>): Int {
        var score = 0
        for (perm in permissions) {
            if (perm.isDangerous) {
                score += 20
            }
        }
        return minOf(score, 100)
    }

    fun calculateRiskLevel(permissions: List<Permission>): String {
        val dangerousCount = permissions.count { it.isDangerous }

        return when {
            dangerousCount >= 4 -> "HIGH"
            dangerousCount >= 2 -> "MEDIUM"
            else -> "LOW"
        }
    }
}