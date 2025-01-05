package com.arafatporosh.hallmanagement.roomrequestadmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.FirebaseDatabase

class RoomRequestActivity : AppCompatActivity() {

    private lateinit var rvRoomRequests: RecyclerView
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val applicationsRef = firebaseDatabase.getReference("applications")
    private val roomsRef = firebaseDatabase.getReference("rooms")

    private val requestList = mutableListOf<RoomRequest>()
    private lateinit var adapter: RoomRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_request)

        rvRoomRequests = findViewById(R.id.rv_room_requests)
        rvRoomRequests.layoutManager = LinearLayoutManager(this)

        adapter = RoomRequestAdapter(requestList, ::onAcceptClicked, ::onRejectClicked)
        rvRoomRequests.adapter = adapter

        loadRequests()
    }

    private fun loadRequests() {
        applicationsRef.get().addOnSuccessListener { snapshot ->
            requestList.clear()
            if (snapshot.exists()) {
                for (data in snapshot.children) {
                    val room = data.child("room").value?.toString() ?: "Unknown Room"
                    val status = data.child("status").value?.toString() ?: "Unknown Status"
                    val studentsSnapshot = data.child("students").children

                    val students = mutableListOf<String>()
                    for (student in studentsSnapshot) {
                        student.value?.toString()?.let {
                            students.add(it)
                        }
                    }

                    val key = data.key ?: ""
                    requestList.add(RoomRequest(room, status, students, key))
                }
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No applications found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load requests: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAcceptClicked(request: RoomRequest) {
        roomsRef.orderByChild("roomName").equalTo(request.room).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                applicationsRef.child(request.key).child("status").setValue("Accepted")
                    .addOnSuccessListener {
                        for (data in snapshot.children) {
                            data.ref.removeValue().addOnSuccessListener {
                                Toast.makeText(this, "Request accepted and room ${request.room} deleted", Toast.LENGTH_SHORT).show()
                                loadRequests()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Failed to delete room: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to accept request", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Room not available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to check room availability: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onRejectClicked(request: RoomRequest) {
        AlertDialog.Builder(this).apply {
            setTitle("Reject Application")
            setMessage("Are you sure you want to reject this application?")
            setPositiveButton("Yes") { _, _ ->
                applicationsRef.child(request.key).child("status").setValue("Rejected")
                    .addOnSuccessListener {
                        Toast.makeText(this@RoomRequestActivity, "Request rejected", Toast.LENGTH_SHORT).show()
                        loadRequests() // Reload data
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@RoomRequestActivity, "Failed to reject request", Toast.LENGTH_SHORT).show()
                    }
            }
            setNegativeButton("No", null)
        }.show()
    }
}