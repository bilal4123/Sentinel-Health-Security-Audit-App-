package com.sentinel.app.compose

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sentinel.app.features.BiometricManager
import com.sentinel.app.models.AppInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ThreatDetailScreen(
    appInfo: AppInfo,
    onBack: () -> Unit
) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val biometricManager = remember { BiometricManager(context) }

    // Biometric Authentication Dialog
    if (showAuthDialog && !isAuthenticated) {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("Security Check") },
            text = { Text("Authenticate to view detailed threat information") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val result = biometricManager.authenticate()
                            if (result) {
                                isAuthenticated = true
                                showAuthDialog = false
                            }
                        }
                    }
                ) {
                    Text("Authenticate")
                }
            },
            dismissButton = {
                TextButton(onClick = onBack) {
                    Text("Cancel")
                }
            }
        )
    }

    if (isAuthenticated) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Threat Analysis") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (appInfo.riskLevel.lowercase()) {
                                "high" -> Color(0xFFFF5252).copy(alpha = 0.1f)
                                "medium" -> Color(0xFFFFA726).copy(alpha = 0.1f)
                                else -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📱 ${appInfo.appName}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = appInfo.packageName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (appInfo.riskLevel.lowercase()) {
                                    "high" -> Color(0xFFFF5252)
                                    "medium" -> Color(0xFFFFA726)
                                    else -> Color(0xFF4CAF50)
                                }
                            ) {
                                Text(
                                    text = "${appInfo.riskLevel} RISK",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Risk Score Card
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Security Score", style = MaterialTheme.typography.titleMedium)
                            AnimatedContent(
                                targetState = appInfo.riskScore,
                                transitionSpec = {
                                    fadeIn() with fadeOut()
                                }
                            ) { score ->
                                Text(
                                    text = "$score",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        score >= 70 -> Color(0xFFFF5252)
                                        score >= 40 -> Color(0xFFFFA726)
                                        else -> Color(0xFF4CAF50)
                                    }
                                )
                            }
                        }
                    }
                }

                // Permissions List
                item {
                    Text(
                        "📋 Permissions (${appInfo.permissions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(appInfo.permissions) { permission ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(permission.icon, fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        permission.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        permission.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (permission.isDangerous) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF4CAF50).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    if (permission.isDangerous) "DANGEROUS" else "NORMAL",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (permission.isDangerous) Color(0xFFFF5252) else Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}