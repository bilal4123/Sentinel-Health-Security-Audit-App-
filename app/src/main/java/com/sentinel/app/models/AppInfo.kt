package com.sentinel.app.models

import java.io.Serializable

data class AppInfo(
    var appName: String,
    var packageName: String,
    var version: String,
    var riskScore: Int = 0,
    var riskLevel: String = "LOW",
    var permissions: List<Permission> = emptyList(),
    var apiLevel: Int = 0,
    var cleartextTraffic: Boolean = false,
    var iconBase64: String = ""
) : Serializable