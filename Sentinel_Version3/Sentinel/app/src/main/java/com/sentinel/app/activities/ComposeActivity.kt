package com.sentinel.app.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.sentinel.app.auth.AuthRepository
import com.sentinel.app.compose.LoginScreen
import com.sentinel.app.compose.SentinelTheme

class ComposeActivity : ComponentActivity() {

    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authRepository = AuthRepository(this)

        setContent {
            SentinelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLoginSuccess = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        },
                        authRepository = authRepository
                    )
                }
            }
        }
    }
}