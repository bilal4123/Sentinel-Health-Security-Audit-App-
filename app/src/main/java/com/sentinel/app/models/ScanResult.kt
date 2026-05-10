package com.sentinel.app.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScanResult(
    val appName: String,
    val packageName: String,
    val riskLevel: String,
    val scanDate: Date,
    val dangerousPermsCount: Int,
    val riskScore: Int
) : Serializable {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        return sdf.format(scanDate)
    }

    fun getRiskColor(): Long {
        return when (riskLevel.lowercase()) {
            "high" -> {
                0xFFFF5252
            }
            "medium" -> {
                0xFFFFA726
            }
            else -> {
                0xFF4CAF50
            }
        }
    }
}