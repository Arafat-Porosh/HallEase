package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class createComplaint : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complaint)

        database = FirebaseDatabase.getInstance().getReference("complaints")

        val complaintHeading = findViewById<EditText>(R.id.et_complaint_heading)
        val complaintDetails = findViewById<EditText>(R.id.et_complaint_details)
        val submitButton = findViewById<Button>(R.id.btn_submit_complaint)


        submitButton.setOnClickListener {
            val heading = complaintHeading.text.toString().trim()
            val details = complaintDetails.text.toString().trim()

            if (heading.isEmpty() || details.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val complaintId = database.push().key
                val complaint = Complaint(
                    id = complaintId,
                    heading = heading,
                    details = details,
                    timestamp = System.currentTimeMillis(),
                    status = "Pending"
                )

                if (complaintId != null) {
                    database.child(complaintId).setValue(complaint).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Complaint submitted successfully!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, complaintHistory::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to submit complaint. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
