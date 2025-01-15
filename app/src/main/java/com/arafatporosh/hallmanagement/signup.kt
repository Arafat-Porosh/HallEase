package com.arafatporosh.hallmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase

class signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val studentName = findViewById<EditText>(R.id.et_student_name)
        val studentID = findViewById<EditText>(R.id.et_student_id)
        val email = findViewById<EditText>(R.id.et_mail)
        val mobileNo = findViewById<EditText>(R.id.et_mobile_no)
        val department = findViewById<EditText>(R.id.et_dept)
        val password = findViewById<EditText>(R.id.et_password)
        val termsCheckBox = findViewById<CheckBox>(R.id.cb_terms_conditions)
        val signUpButton = findViewById<Button>(R.id.btn_sign_up)
        val clickHere = findViewById<TextView>(R.id.tv_click_here)

        clickHere.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val name = studentName.text.toString().trim()
            val id = studentID.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val mobile = mobileNo.text.toString().trim()
            val dept = department.text.toString().trim()
            val pass = password.text.toString().trim()

            if (name.isEmpty() || id.isEmpty() || userEmail.isEmpty() || mobile.isEmpty() || dept.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show()
            } else {
                createNewUser(name, id, userEmail, mobile, dept, pass)
            }
        }
    }

    private fun createNewUser(name: String, id: String, userEmail: String, mobile: String, dept: String, password: String) {
        auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.getReference("users").child(user.uid)

                        val userData = mapOf(
                            "name" to name,
                            "studentID" to id,
                            "email" to userEmail,
                            "mobileNo" to mobile,
                            "dept" to dept
                        )

                        userRef.setValue(userData)
                            .addOnCompleteListener {
                                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, login::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    handleSignUpError(task.exception)
                }
            }
    }

    private fun handleSignUpError(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Weak password! Use at least 6 characters."
            is FirebaseAuthInvalidCredentialsException -> "Invalid email format."
            is FirebaseAuthUserCollisionException -> "Email already in use."
            else -> "Sign-up failed: ${exception?.message}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        Log.e("SignupActivity", "Error: ${exception?.message}")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, login::class.java)
        startActivity(intent)
        finish()
    }

}
