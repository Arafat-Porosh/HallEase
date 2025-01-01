package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.et_cuet_mail)
        val passwordField = findViewById<EditText>(R.id.et_password)
        val signInButton = findViewById<Button>(R.id.btn_sign_in)
        val forgotPasswordText = findViewById<TextView>(R.id.tv_forgot_password)
        val signUpButton = findViewById<TextView>(R.id.tv_sign_up)

        signUpButton.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }


        signInButton.setOnClickListener {
            val userEmail = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (userEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                authenticateUser(userEmail, password)
            }
        }

        forgotPasswordText.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }

    private fun authenticateUser(userEmail: String, password: String) {
        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.getReference("users").child(user.uid)

                        userRef.get().addOnSuccessListener { dataSnapshot ->
                            if (dataSnapshot.exists()) {
                                val userType = dataSnapshot.child("email").value.toString()

                                if (userEmail == "bbhall@cuet.ac.bd") {
                                    // Navigate to Admin Dashboard
//                                    val intent = Intent(this, AdminDashboardActivity::class.java)
//                                    startActivity(intent)
                                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Navigate to Student Dashboard
                                    val intent = Intent(this, stuDashboard::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "User data not found in database.", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to fetch user data: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
