package com.arafatporosh.hallmanagement.roomrequestadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arafatporosh.hallmanagement.AdminDashboard
import com.arafatporosh.hallmanagement.R
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.io.InputStream

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

                    val submittedBy = data.child("submittedBy").value?.toString() ?: "Unknown"
                    val key = data.key ?: ""
                    requestList.add(RoomRequest(room, status, students, key, submittedBy))
                }

                requestList.sortBy { it.status != "Pending" }

                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No applications found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load requests: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAcceptClicked(request: RoomRequest) {
        AlertDialog.Builder(this).apply {
            setTitle("Accept Application")
            setMessage("Are you sure you want to accept this application for room ${request.room}?")
            setPositiveButton("Yes") { _, _ ->
                roomsRef.orderByChild("roomName").equalTo(request.room).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        applicationsRef.child(request.key).child("status").setValue("Accepted")
                            .addOnSuccessListener {
                                for (data in snapshot.children) {
                                    data.ref.removeValue().addOnSuccessListener {
                                        sendNotificationToStudent(request.submittedBy, "Accepted")
                                        Toast.makeText(
                                            this@RoomRequestActivity,
                                            "Request accepted and room ${request.room} deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        loadRequests()
                                    }.addOnFailureListener {
                                        Toast.makeText(
                                            this@RoomRequestActivity,
                                            "Failed to delete room: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@RoomRequestActivity,
                                    "Failed to accept request",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this@RoomRequestActivity,
                            "Room not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        this@RoomRequestActivity,
                        "Failed to check room availability: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setNegativeButton("No", null)
        }.show()
    }

    private fun onRejectClicked(request: RoomRequest) {
        AlertDialog.Builder(this).apply {
            setTitle("Reject Application")
            setMessage("Are you sure you want to reject this application?")
            setPositiveButton("Yes") { _, _ ->
                applicationsRef.child(request.key).child("status").setValue("Rejected")
                    .addOnSuccessListener {
                        sendNotificationToStudent(request.submittedBy, "Rejected")
                        Toast.makeText(this@RoomRequestActivity, "Request rejected", Toast.LENGTH_SHORT).show()
                        loadRequests()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@RoomRequestActivity, "Failed to reject request", Toast.LENGTH_SHORT).show()
                    }
            }
            setNegativeButton("No", null)
        }.show()
    }

    private fun sendNotificationToStudent(studentID: String, status: String) {
        Log.d("Notification", "Sending notification and email to student: $studentID")

        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.orderByChild("studentID").equalTo(studentID).get()
            .addOnSuccessListener { snapshot ->
                Log.d("Notification", "Query Success: Found ${snapshot.childrenCount} results")
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val fcmToken = child.child("fcmToken").value.toString()
                        val studentEmail = child.child("email").value.toString()

                        Log.d("Notification", "Retrieved FCM Token: $fcmToken")
                        Log.d("Notification", "Retrieved Email: $studentEmail")

                        if (fcmToken.isNotEmpty() && fcmToken.length > 50) {
                            sendFCMNotification(fcmToken, status)
                        } else {
                            Log.e("FCM", "Invalid FCM Token")
                        }

                        if (studentEmail.isNotEmpty()) {
                            sendEmailToStudent(studentEmail, status)
                        } else {
                            Log.e("Email", "No email found for student: $studentID")
                        }

                        return@addOnSuccessListener
                    }
                } else {
                    Log.e("Notification", "No user found for studentID: $studentID")
                }
            }
            .addOnFailureListener {
                Log.e("Notification", "Failed to retrieve user data", it)
            }
    }

    private fun sendEmailToStudent(email: String, status: String) {
        val subject = "Your Application Has Been Updated"
        val message = "Dear Student,\n\nYour application for room has been '$status'\n\nThank you for your patience.\n\nBest Regards,\nHallEase"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            this@RoomRequestActivity.startActivity(Intent.createChooser(intent, "Send Email via..."))
            Log.d("Email", "Email sent successfully to $email")
        } catch (e: Exception) {
            Log.e("Email", "Failed to send email", e)
        }
    }

    private fun sendFCMNotification(fcmToken: String, status: String) {
        val notificationData = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", fcmToken)
                put("data", JSONObject().apply {
                    put("title", "Application Status Updated")
                    put("body", "Your application for room has been '$status'.")
                    put("click_action", "OPEN_ACTIVITY")
                })
            })
        }

        val url = "https://fcm.googleapis.com/v1/projects/hallmanageme/messages:send"
        val request = object : StringRequest(Method.POST, url,
            { response -> Log.d("FCM", "Notification sent: $response") },
            { error -> Log.e("FCM", "Error sending notification", error) }) {

            override fun getHeaders(): Map<String, String> {
                return mapOf(
                    "Authorization" to "Bearer ${getAccessToken()}",
                    "Content-Type" to "application/json"
                )
            }

            override fun getBody(): ByteArray {
                val body = notificationData.toString()
                Log.d("FCM", "FCM Request Body: $body")
                return body.toByteArray(Charsets.UTF_8)
            }
        }

        Volley.newRequestQueue(this@RoomRequestActivity).add(request)
    }

    private fun getAccessToken(): String {
        return try {
            val assetManager = this@RoomRequestActivity.assets
            val inputStream: InputStream = assetManager.open("hallmanageme-firebase-adminsdk.json")

            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

            credentials.refreshIfExpired()
            val token = credentials.accessToken.tokenValue
            Log.d("FCM", "Generated OAuth Token: $token")
            token
        } catch (e: Exception) {
            Log.e("FCM", "Failed to generate OAuth Token", e)
            ""
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AdminDashboard::class.java)
        startActivity(intent)
        finish()
    }
}