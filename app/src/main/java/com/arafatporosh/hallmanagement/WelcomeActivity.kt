package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Simulate a delay before navigating to the Login Page
        Handler(Looper.getMainLooper()).postDelayed({
            // Navigate to Login Page
            val intent = Intent(this@WelcomeActivity, login::class.java)
            startActivity(intent)
            finish() // Close Welcome screen
        }, 5000) // Delay for 3 seconds
    }
}
