package com.arafatporosh.hallmanagement.admincomplaints

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arafatporosh.hallmanagement.Complaints
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.FirebaseDatabase
import com.google.auth.oauth2.GoogleCredentials
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ComplaintAdapter(
    private val complaintsList: MutableList<Complaints>,
    private val context: Context
) : RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    inner class ComplaintViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardViewComplaint)
        val title: TextView = itemView.findViewById(R.id.tv_complaint_title1)
        val details: TextView = itemView.findViewById(R.id.tv_complaint_details1)
        val studentID: TextView = itemView.findViewById(R.id.tv_complaint_by_ID)
        val category: TextView = itemView.findViewById(R.id.tv_category_name)
        val date: TextView = itemView.findViewById(R.id.tv_complaint_date1)
        val status: TextView = itemView.findViewById(R.id.tv_complaint_status1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaintsList[position]
        holder.title.text = complaint.heading
        holder.details.text = complaint.details
        holder.studentID.text = complaint.studentID
        holder.category.text = complaint.type
        holder.date.text = complaint.timestamp.toFormattedDate()
        holder.status.text = complaint.status

        when (complaint.status) {
            "Pending" -> {
                holder.status.text = "Pending"
                holder.status.setTextColor(context.getColor(R.color.red))
            }
            "Accepted" -> {
                holder.status.text = "Resolved"
                holder.status.setTextColor(context.getColor(R.color.green))
                holder.cardView.findViewById<TextView>(R.id.textView6).visibility = View.GONE
            }
            else -> {
                holder.status.text = "Unknown"
                holder.status.setTextColor(context.getColor(R.color.gray))
            }
        }

        holder.cardView.setOnClickListener {
            if (complaint.status == "Pending") {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Resolve Complaint")
                builder.setMessage("Has the problem been solved?")

                builder.setPositiveButton("Yes") { _, _ ->
                    val database = FirebaseDatabase.getInstance().getReference("complaints")
                    complaint.id?.let { id ->
                        database.child(id).child("status").setValue("Accepted").addOnCompleteListener {
                            if (it.isSuccessful) {
                                complaint.status = "Accepted"
                                notifyItemChanged(position)
                                sendNotificationToStudent(complaint.studentID, complaint.heading)
                            } else {
                                Log.e("ComplaintAdapter", "Failed to update status for: ${complaint.heading}")
                            }
                        }
                    }
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            } else if (complaint.status == "Accepted") {
                AlertDialog.Builder(context)
                    .setTitle("Complaint Resolved")
                    .setMessage("This complaint is already resolved.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = complaintsList.size

    private fun sendNotificationToStudent(studentID: String, complaintTitle: String) {
        Log.d("Notification", "Sending notification and email to student: $studentID")

        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
        databaseRef.orderByChild("studentID").equalTo(studentID).get()
            .addOnSuccessListener { snapshot ->
                Log.d("Notification", "Query Success: Found ${snapshot.childrenCount} results")
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val fcmToken = child.child("fcmToken").value.toString()
                        val studentEmail = child.child("email").value.toString() // ✅ Get student's email

                        Log.d("Notification", "Retrieved FCM Token: $fcmToken")
                        Log.d("Notification", "Retrieved Email: $studentEmail")

                        // 🔥 Send Push Notification
                        if (fcmToken.isNotEmpty() && fcmToken.length > 50) {
                            sendFCMNotification(fcmToken, complaintTitle)
                        } else {
                            Log.e("FCM", "Invalid FCM Token")
                        }

                        // ✉️ Send Email Notification
                        if (studentEmail.isNotEmpty()) {
                            sendEmailToStudent(studentEmail, complaintTitle)
                        } else {
                            Log.e("Email", "No email found for student: $studentID")
                        }

                        return@addOnSuccessListener // Exit after first match
                    }
                } else {
                    Log.e("Notification", "No user found for studentID: $studentID")
                }
            }
            .addOnFailureListener {
                Log.e("Notification", "Failed to retrieve user data", it)
            }
    }


    private fun sendEmailToStudent(email: String, complaintTitle: String) {
        val subject = "Your Complaint Has Been Resolved"
        val message = "Dear Student,\n\nYour complaint titled '$complaintTitle' has been resolved.\n\nThank you for your patience.\n\nBest Regards,\nHallEase"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email via..."))
            Log.d("Email", "Email sent successfully to $email")
        } catch (e: Exception) {
            Log.e("Email", "Failed to send email", e)
        }
    }


    private fun sendFCMNotification(fcmToken: String, complaintTitle: String) {
        val notificationData = JSONObject().apply {
            put("message", JSONObject().apply {
                put("token", fcmToken)
                put("data", JSONObject().apply {  // Use "data" instead of "notification"
                    put("title", "Complaint Resolved")
                    put("body", "Your complaint '$complaintTitle' has been resolved.")
                    put("click_action", "OPEN_ACTIVITY") // Custom action
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
                Log.d("FCM", "FCM Request Body: $body") // Log the request body
                return body.toByteArray(Charsets.UTF_8)
            }
        }

        Volley.newRequestQueue(context).add(request)
    }


    private fun getAccessToken(): String {
        return try {
            val assetManager = context.assets
            val inputStream: InputStream = assetManager.open("hallmanageme-firebase-adminsdk.json")

            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

            credentials.refreshIfExpired()
            val token = credentials.accessToken.tokenValue
            Log.d("FCM", "Generated OAuth Token: $token") // Log the generated token
            token
        } catch (e: Exception) {
            Log.e("FCM", "Failed to generate OAuth Token", e)
            ""
        }
    }


    private fun Long.toFormattedDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(Date(this))
    }
}
