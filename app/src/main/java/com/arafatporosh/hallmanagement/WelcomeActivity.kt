package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                navigateToDashboard(currentUser.email)
            } else {
                val intent = Intent(this@WelcomeActivity, login::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000)
    }

    private fun navigateToDashboard(userEmail: String?) {
        if (userEmail == "bbhall@cuet.ac.bd") {
            val intent = Intent(this, AdminDashboard::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, stuDashboard::class.java)
            startActivity(intent)
        }
    }
}
