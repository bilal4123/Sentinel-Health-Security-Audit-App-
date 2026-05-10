package com.sentinel.app.models

import java.io.Serializable

data class Permission(
    val name: String,
    val protectionLevel: String,
    val icon: String
) : Serializable {

    val isDangerous: Boolean
        get() = protectionLevel.equals("dangerous", ignoreCase = true) ||
                protectionLevel.equals("DANGEROUS", ignoreCase = true)

    val description: String
        get() = when (name) {
            "ACCESS_FINE_LOCATION" -> "Access precise location from GPS and network sources"
            "CAMERA" -> "Take pictures and record videos"
            "RECORD_AUDIO" -> "Record audio through microphone"
            "READ_CONTACTS" -> "Read user's contacts data"
            "WRITE_EXTERNAL_STORAGE" -> "Write to external storage"
            else -> "Access device features and data"
        }
}