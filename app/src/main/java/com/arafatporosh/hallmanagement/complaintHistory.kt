package com.arafatporosh.hallmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class complaintHistory : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var complaintsRecyclerView: RecyclerView
    private lateinit var complaintsAdapter: ComplaintHistoryAdapter
    private val complaintsList = mutableListOf<Complaint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_history)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("complaints")

        // Setup RecyclerView
        complaintsRecyclerView = findViewById(R.id.rv_complaint_history)
        complaintsRecyclerView.layoutManager = LinearLayoutManager(this)
        complaintsAdapter = ComplaintHistoryAdapter(complaintsList)
        complaintsRecyclerView.adapter = complaintsAdapter

        // Fetch complaints from Firebase
        fetchComplaints()
    }

    private fun fetchComplaints() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintsList.clear() // Clear the list to avoid duplicate data
                for (complaintSnapshot in snapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaint::class.java)
                    if (complaint != null) {
                        complaintsList.add(complaint)
                    }
                }
                if (complaintsList.isEmpty()) {
                    Toast.makeText(this@complaintHistory, "No complaints found.", Toast.LENGTH_SHORT).show()
                }
                complaintsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@complaintHistory, "Failed to fetch complaints: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
