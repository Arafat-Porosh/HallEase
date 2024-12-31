package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class stuDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_dashboard)

        // Set click listeners for the cards
        findViewById<CardView>(R.id.card_create_complaint).setOnClickListener {
            val intent = Intent(this, createComplaint::class.java)
            startActivity(intent)
        }
        findViewById<CardView>(R.id.card_complaint_history).setOnClickListener {
            Toast.makeText(this, "Complaint History Clicked", Toast.LENGTH_SHORT).show()
        }
        findViewById<CardView>(R.id.card_apply_seat).setOnClickListener {
            Toast.makeText(this, "Apply for Seat Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}