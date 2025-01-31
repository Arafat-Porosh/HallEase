package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class createComplaint : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complaint)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("complaints")

        val complaintHeading = findViewById<EditText>(R.id.et_complaint_heading)
        val complaintDetails = findViewById<EditText>(R.id.et_complaint_details)
        val complaintTypeSpinner = findViewById<Spinner>(R.id.spinner_complaint_type)
        val submitButton = findViewById<Button>(R.id.btn_submit_complaint)

        submitButton.setOnClickListener {
            val heading = complaintHeading.text.toString().trim()
            val details = complaintDetails.text.toString().trim()
            val complaintType = complaintTypeSpinner.selectedItem.toString()
            val userId = auth.currentUser?.uid

            if (heading.isEmpty() || details.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                userRef.get().addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        val studentID = dataSnapshot.child("studentID").value.toString()
                        val complaintId = database.push().key

                        val complaint = Complaint(
                            id = complaintId,
                            studentID = studentID, // Fetch studentID from users database
                            heading = heading,
                            details = details,
                            type = complaintType,
                            timestamp = System.currentTimeMillis(),
                            status = "Pending"
                        )

                        if (complaintId != null) {
                            database.child(complaintId).setValue(complaint).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, complaintHistory::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to submit complaint. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Student data not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error fetching student data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, stuDashboard::class.java)
        startActivity(intent)
        finish()
    }
}
