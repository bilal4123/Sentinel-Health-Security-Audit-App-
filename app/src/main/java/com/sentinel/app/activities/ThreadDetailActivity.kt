package com.sentinel.app.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sentinel.app.compose.SentinelTheme
import com.sentinel.app.compose.ThreatDetailScreen
import com.sentinel.app.models.AppInfo
import com.sentinel.app.models.Permission

class ThreatDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get data from Intent
        val appName = intent.getStringExtra("app_name") ?: "Unknown App"
        val packageName = intent.getStringExtra("package_name") ?: "unknown.package"
        val riskLevel = intent.getStringExtra("risk_level") ?: "LOW"
        val riskScore = intent.getIntExtra("risk_score", 0)

        // Get permissions list (if passed)
        val permissionNames = intent.getStringArrayListExtra("permissions") ?: ArrayList()

        // Create AppInfo object with sample permissions (you can enhance this)
        val permissions = permissionNames.map { permName ->
            Permission(
                name = permName,
                protectionLevel = if (permName.contains("LOCATION") ||
                    permName.contains("CAMERA") ||
                    permName.contains("RECORD") ||
                    permName.contains("CONTACTS") ||
                    permName.contains("STORAGE")) "dangerous" else "normal",
                icon = when {
                    permName.contains("LOCATION") -> "📍"
                    permName.contains("CAMERA") -> "📷"
                    permName.contains("RECORD") -> "🎤"
                    permName.contains("CONTACTS") -> "👥"
                    permName.contains("STORAGE") -> "💾"
                    else -> "🔘"
                }
            )
        }

        val appInfo = AppInfo(
            appName = appName,
            packageName = packageName,
            version = "",
            riskScore = riskScore,
            riskLevel = riskLevel,
            permissions = permissions,
            apiLevel = 0,
            cleartextTraffic = false
        )

        setContent {
            SentinelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ThreatDetailScreen(
                        appInfo = appInfo,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}