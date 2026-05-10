package com.sentinel.app.models

import java.util.Date

data class UserProfile(
    val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val deviceName: String = "",
    val lastActive: Date = Date(),
    val totalScans: Int = 0,
    val highRiskDetected: Int = 0
)