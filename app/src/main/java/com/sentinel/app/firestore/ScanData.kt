package com.sentinel.app.models

import java.util.Date

data class ScanData(
    val id: String = "",
    val appName: String,
    val packageName: String,
    val version: String,
    val riskScore: Int,
    val riskLevel: String,
    val dangerousPermsCount: Int,
    val permissions: List<String>,
    val scanDate: Date,
    val userId: String
) {
    fun toScanResult(): ScanResult {
        return ScanResult(
            appName = appName,
            packageName = packageName,
            riskLevel = riskLevel,
            scanDate = scanDate,
            dangerousPermsCount = dangerousPermsCount,
            riskScore = riskScore
        )
    }
}