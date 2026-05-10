package com.sentinel.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sentinel.app.R
import com.sentinel.app.activities.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val etDeviceName = findViewById<EditText>(R.id.et_device_name)
        val btnStart = findViewById<Button>(R.id.btn_start)

        btnStart.setOnClickListener {
            val deviceName = etDeviceName.text.toString()

            if (deviceName.isNotBlank()) {
                // Navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("DEVICE_NAME", deviceName)
                startActivity(intent)
                finish() // Close splash so back button doesn't return here
            } else {
                Toast.makeText(this, "Please enter a device name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}