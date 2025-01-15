package com.arafatporosh.hallmanagement.Roominfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.AdminDashboard
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.FirebaseDatabase

class RoomInformation : AppCompatActivity() {

    private lateinit var rvRooms: RecyclerView
    private lateinit var etNewRoom: EditText
    private lateinit var btnAddRoom: Button

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val roomsRef = firebaseDatabase.getReference("rooms")

    private val roomList = mutableListOf<String>()
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_information)

        rvRooms = findViewById(R.id.rv_rooms)
        etNewRoom = findViewById(R.id.editTextText)
        btnAddRoom = findViewById(R.id.button)

        roomAdapter = RoomAdapter(roomList) { roomName ->
            showRoomDialog(roomName)
        }

        rvRooms.layoutManager = GridLayoutManager(this, 3)
        rvRooms.adapter = roomAdapter

        loadRooms()

        btnAddRoom.setOnClickListener {
            val newRoom = etNewRoom.text.toString().trim()
            if (newRoom.isNotEmpty()) {
                addNewRoom(newRoom)
            } else {
                Toast.makeText(this, "Enter a valid room name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRooms() {
        roomsRef.get().addOnSuccessListener { snapshot ->
            roomList.clear()
            if (snapshot.exists()) {
                for (data in snapshot.children) {
                    val roomName = data.child("roomName").value.toString()
                    if (roomName.isNotEmpty()) {
                        roomList.add(roomName)
                    }
                }
            }
            roomAdapter.notifyDataSetChanged()
        }
    }

    private fun addNewRoom(roomName: String) {
        val roomData = mapOf("roomName" to roomName)
        roomsRef.push().setValue(roomData).addOnSuccessListener {
            etNewRoom.text.clear()
            roomList.add(roomName)
            roomAdapter.notifyItemInserted(roomList.size - 1)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRoomDialog(roomName: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Room Options")
            setMessage("Has the room $roomName been allotted?")
            setPositiveButton("Yes") { _, _ ->
                deleteRoom(roomName)
            }
            setNegativeButton("No", null)
        }.show()
    }

    private fun deleteRoom(roomName: String) {
        roomsRef.orderByChild("roomName").equalTo(roomName).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        data.ref.removeValue().addOnSuccessListener {
                            roomList.remove(roomName)
                            roomAdapter.notifyDataSetChanged()
                            Toast.makeText(this, "Room deleted successfully", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to delete room: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Room not found in database", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to query room: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminDashboard::class.java)
        startActivity(intent)
        finish()
    }

}
