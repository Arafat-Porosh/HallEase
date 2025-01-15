package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class StudentInfo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_info)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StudentAdapter(studentList, this)
        recyclerView.adapter = adapter

        fetchStudentsFromFirebase()
    }

    private fun fetchStudentsFromFirebase() {
        val db = FirebaseDatabase.getInstance().getReference("users")
        db.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                studentList.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val student = userSnapshot.getValue(Student::class.java)
                    if (student != null) {
                        studentList.add(student)
                    } else {
                        Log.e("StudentInfo", "Failed to parse student data: $userSnapshot")
                    }
                }
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No student data found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve data: ${it.message}", Toast.LENGTH_SHORT).show()
            Log.e("StudentInfo", "Error fetching data", it)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminDashboard::class.java)
        startActivity(intent)
        finish()
    }
}
