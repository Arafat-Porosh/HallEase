package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class stuDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_dashboard)

        auth = FirebaseAuth.getInstance()

//        val userNameTextView = findViewById<TextView>(R.id.UserName)
//        val username = intent.getStringExtra("username")
//        userNameTextView.text = username ?: "Student"
        userNameTextView = findViewById(R.id.UserName)
        fetchUserName()
        saveFCMToken()
        requestNotificationPermission()

        findViewById<CardView>(R.id.card_create_complaint).setOnClickListener {
            val intent = Intent(this, createComplaint::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<CardView>(R.id.card_complaint_history).setOnClickListener {
            Toast.makeText(this, "Complaint History Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, complaintHistory::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<CardView>(R.id.card_apply_seat).setOnClickListener {
            Toast.makeText(this, "Apply for Seat Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RoomApply::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Dashboard selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchUserName() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val studentName = dataSnapshot.child("name").value.toString()
                    userNameTextView.text = studentName
                } else {
                    userNameTextView.text = "Student"
                }
            }.addOnFailureListener {
                userNameTextView.text = "Student"
            }
        } else {
            userNameTextView.text = "Student"
        }
    }

    private fun saveFCMToken() {
        val userId = auth.currentUser?.uid ?: return
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM", "FCM token generation failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "FCM Token: $token")

            FirebaseDatabase.getInstance().getReference("users").child(userId).child("fcmToken")
                .setValue(token)
                .addOnSuccessListener { Log.d("FCM", "FCM Token saved successfully") }
                .addOnFailureListener { Log.e("FCM", "Failed to save FCM token", it) }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }



    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(this@stuDashboard, login::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton("No", null)
            setCancelable(true)
        }.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this).apply {
            setTitle("Exit App")
            setMessage("Are you sure you want to exit?")
            setPositiveButton("Yes") { _, _ ->
                finishAffinity()
            }
            setNegativeButton("No", null)
            setCancelable(true)
        }.show()
    }
}
