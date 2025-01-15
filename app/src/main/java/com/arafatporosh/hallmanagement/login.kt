package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
            finish()
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
            finish()
        }
    }

    private fun authenticateUser(userEmail: String, password: String) {
        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (userEmail == "bbhall@cuet.ac.bd") {
                            promptAdminPassword { isAdminPasswordCorrect ->
                                if (isAdminPasswordCorrect) {
                                    val intent = Intent(this, AdminDashboard::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "Invalid admin password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val intent = Intent(this, stuDashboard::class.java)
                            startActivity(intent)
                            Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun promptAdminPassword(callback: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Admin Authentication")

        val input = EditText(this)
        input.hint = "Enter Admin Password"
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val enteredPassword = input.text.toString().trim()
            callback(enteredPassword == "admin")
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            callback(false)
        }
        builder.show()
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
