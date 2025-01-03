package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Reference each CardView
        val studentInfoCard = findViewById<CardView>(R.id.cardStudentInfo)
        val roomRequestCard = findViewById<CardView>(R.id.cardRoomRequest)
        val complaintCard = findViewById<CardView>(R.id.cardComplaint)
        val availableRoomsCard = findViewById<CardView>(R.id.cardAvailableRooms)

        // Set click listeners for each card
        studentInfoCard.setOnClickListener {
            Toast.makeText(this, "Student Information Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Student Information Activity
            // val intent = Intent(this, StudentInformationActivity::class.java)
            // startActivity(intent)
        }

        roomRequestCard.setOnClickListener {
            Toast.makeText(this, "Room Request Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Room Request Activity
            // val intent = Intent(this, RoomRequestActivity::class.java)
            // startActivity(intent)
        }

        complaintCard.setOnClickListener {
            Toast.makeText(this, "Complaint Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Complaint Activity
            // val intent = Intent(this, ComplaintActivity::class.java)
            // startActivity(intent)
        }

        availableRoomsCard.setOnClickListener {
            Toast.makeText(this, "Available Rooms Clicked", Toast.LENGTH_SHORT).show()
            // Navigate to the Available Rooms Activity
            // val intent = Intent(this, AvailableRoomsActivity::class.java)
            // startActivity(intent)
        }
    }
}