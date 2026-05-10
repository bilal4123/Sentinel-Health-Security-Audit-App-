package com.sentinel.app.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sentinel.app.R
import kotlinx.coroutines.tasks.await

class GoogleSignInClientProvider(private val context: Context) {

    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return getClient().signInIntent
    }

    suspend fun signOut() {
        getClient().signOut().await()
    }
}