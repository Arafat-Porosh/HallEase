package com.arafatporosh.hallmanagement.admincomplaints

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.AdminDashboard
import com.arafatporosh.hallmanagement.Complaints
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.FirebaseDatabase

class ComplaintActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComplaintAdapter
    private val complaintsList = mutableListOf<Complaints>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint)
        val summarybtn = findViewById<Button>(R.id.summaryButton)
        recyclerView = findViewById(R.id.recyclerViewComplaints)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ComplaintAdapter(complaintsList, this@ComplaintActivity)
        recyclerView.adapter = adapter

        fetchComplaintsFromFirebase()

        summarybtn.setOnClickListener {
            val intent = Intent(this, ComplaintCategoryChartActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchComplaintsFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("complaints")
        database.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                complaintsList.clear()
                for (complaintSnapshot in dataSnapshot.children) {
                    val complaint = complaintSnapshot.getValue(Complaints::class.java)
                    if (complaint != null) {
                        complaintsList.add(complaint)
                    }
                }

                complaintsList.sortWith(compareBy { it.status != "Pending" })

                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No complaints found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load complaints: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminDashboard::class.java)
        startActivity(intent)
        finish()
    }

}
