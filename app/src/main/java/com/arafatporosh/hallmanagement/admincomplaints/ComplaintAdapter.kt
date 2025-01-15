package com.arafatporosh.hallmanagement.admincomplaints

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.R
import com.google.firebase.database.FirebaseDatabase
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
        holder.date.text = complaint.timestamp.toFormattedDate()

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

    private fun Long.toFormattedDate(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(Date(this))
    }
}
