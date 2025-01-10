package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.arafatporosh.hallmanagement.Roominfo.RoomInformation
import com.arafatporosh.hallmanagement.admincomplaints.ComplaintActivity
import com.arafatporosh.hallmanagement.roomrequestadmin.RoomRequestActivity

class AdminDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val studentInfoCard = findViewById<CardView>(R.id.cardStudentInfo)
        val roomRequestCard = findViewById<CardView>(R.id.cardRoomRequest)
        val complaintCard = findViewById<CardView>(R.id.cardComplaint)
        val availableRoomsCard = findViewById<CardView>(R.id.cardAvailableRooms)

        studentInfoCard.setOnClickListener {
            Toast.makeText(this, "Student Information Clicked", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, StudentInfo::class.java)
             startActivity(intent)
        }

        roomRequestCard.setOnClickListener {
            Toast.makeText(this, "Room Request Clicked", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, RoomRequestActivity::class.java)
             startActivity(intent)
        }

        complaintCard.setOnClickListener {
            Toast.makeText(this, "Complaint Clicked", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, ComplaintActivity::class.java)
             startActivity(intent)
        }

        availableRoomsCard.setOnClickListener {
            Toast.makeText(this, "Available Rooms Clicked", Toast.LENGTH_SHORT).show()
             val intent = Intent(this, RoomInformation::class.java)
             startActivity(intent)
        }
    }
}