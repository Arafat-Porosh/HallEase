package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RoomApply : AppCompatActivity() {

    private lateinit var spinnerRoomOptions: Spinner
    private lateinit var btnApply: Button
    private lateinit var etStudent1: EditText
    private lateinit var etStudent2: EditText
    private lateinit var etStudent3: EditText
    private lateinit var etStudent4: EditText

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val roomsRef = firebaseDatabase.getReference("rooms")
    private val applicationsRef = firebaseDatabase.getReference("applications")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_apply)

        spinnerRoomOptions = findViewById(R.id.spinner_room_options)
        btnApply = findViewById(R.id.btn_apply)
        etStudent1 = findViewById(R.id.et_student_1)
        etStudent2 = findViewById(R.id.et_student_2)
        etStudent3 = findViewById(R.id.et_student_3)
        etStudent4 = findViewById(R.id.et_student_4)

        loadRoomsIntoSpinner()

        btnApply.setOnClickListener {
            submitApplication()
        }
    }

    private fun loadRoomsIntoSpinner() {
        roomsRef.get().addOnSuccessListener { snapshot ->
            val roomsList = mutableListOf<String>()

            if (snapshot.exists()) {
                for (data in snapshot.children) {
                    val roomName = data.child("roomName").value.toString()
                    if (roomName.isNotEmpty()) {
                        roomsList.add(roomName)
                    }
                }

                if (roomsList.isEmpty()) {
                    roomsList.addAll(listOf("A502", "A503", "A504"))
                }
            } else {
                roomsList.addAll(listOf("A502", "A503", "A504"))
            }

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                roomsList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRoomOptions.adapter = adapter
            spinnerRoomOptions.isEnabled = true
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load rooms: ${exception.message}", Toast.LENGTH_SHORT).show()

            val roomsList = listOf("Dummy Room 1", "Dummy Room 2", "Dummy Room 3")
            val adapter = ArrayAdapter(
                this,
                R.layout.custom_spinner_item,
                roomsList
            )
            adapter.setDropDownViewResource(R.layout.custom_spinner_item)
            spinnerRoomOptions.adapter = adapter
            spinnerRoomOptions.isEnabled = true
        }
    }


    private fun submitApplication() {
        val selectedRoom = spinnerRoomOptions.selectedItem?.toString()
        val student1 = etStudent1.text.toString().trim()
        val student2 = etStudent2.text.toString().trim()
        val student3 = etStudent3.text.toString().trim()
        val student4 = etStudent4.text.toString().trim()

        if (selectedRoom.isNullOrEmpty() || student1.isEmpty()) {
            Toast.makeText(this, "Please select a room and provide at least one Student ID", Toast.LENGTH_SHORT).show()
            return
        }

        val applicationData = mapOf(
            "room" to selectedRoom,
            "students" to listOf(student1, student2, student3, student4).filter { it.isNotEmpty() },
            "status" to "Pending"
        )
        applicationsRef.push().setValue(applicationData).addOnSuccessListener {
            Toast.makeText(this, "Application submitted successfully", Toast.LENGTH_SHORT).show()
            clearFields()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to submit application", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        spinnerRoomOptions.setSelection(0)
        etStudent1.text.clear()
        etStudent2.text.clear()
        etStudent3.text.clear()
        etStudent4.text.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, stuDashboard::class.java)
        startActivity(intent)
        finish()
    }

}
