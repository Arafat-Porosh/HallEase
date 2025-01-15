package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class stuDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_dashboard)

        auth = FirebaseAuth.getInstance()

        findViewById<CardView>(R.id.card_create_complaint).setOnClickListener {
            val intent = Intent(this, createComplaint::class.java)
            startActivity(intent)
        }
        findViewById<CardView>(R.id.card_complaint_history).setOnClickListener {
            Toast.makeText(this, "Complaint History Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, complaintHistory::class.java)
            startActivity(intent)
        }
        findViewById<CardView>(R.id.card_apply_seat).setOnClickListener {
            Toast.makeText(this, "Apply for Seat Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RoomApply::class.java)
            startActivity(intent)
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Dashboard selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
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
