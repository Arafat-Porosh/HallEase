package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.arafatporosh.hallmanagement.Roominfo.RoomInformation
import com.arafatporosh.hallmanagement.admincomplaints.ComplaintActivity
import com.arafatporosh.hallmanagement.roomrequestadmin.RoomRequestActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class AdminDashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        auth = FirebaseAuth.getInstance()

        val studentInfoCard = findViewById<CardView>(R.id.cardStudentInfo)
        val roomRequestCard = findViewById<CardView>(R.id.cardRoomRequest)
        val complaintCard = findViewById<CardView>(R.id.cardComplaint)
        val availableRoomsCard = findViewById<CardView>(R.id.cardAvailableRooms)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        studentInfoCard.setOnClickListener {
            val intent = Intent(this, StudentInfo::class.java)
            startActivity(intent)
            finish()
        }

        roomRequestCard.setOnClickListener {
            val intent = Intent(this, RoomRequestActivity::class.java)
            startActivity(intent)
            finish()
        }

        complaintCard.setOnClickListener {
            val intent = Intent(this, ComplaintActivity::class.java)
            startActivity(intent)
            finish()
        }

        availableRoomsCard.setOnClickListener {
            val intent = Intent(this, RoomInformation::class.java)
            startActivity(intent)
            finish()
        }
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Dashboard selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    handleLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun handleLogout() {
        AlertDialog.Builder(this).apply {
            setTitle("Logout")
            setMessage("Are you sure you want to log out?")
            setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(this@AdminDashboard, login::class.java)
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
