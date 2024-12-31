package com.arafatporosh.hallmanagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComplaintHistoryAdapter(private val complaints: List<Complaint>) :
    RecyclerView.Adapter<ComplaintHistoryAdapter.ComplaintViewHolder>() {

    inner class ComplaintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_complaint_title)
        val date: TextView = view.findViewById(R.id.tv_complaint_date)
        val status: TextView = view.findViewById(R.id.tv_complaint_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaint_history, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        holder.title.text = complaint.heading
        holder.date.text = java.text.DateFormat.getDateTimeInstance().format(complaint.timestamp)
        holder.status.text = "Status: ${complaint.status}" // Dynamically set the status

        // Update text color based on status
        holder.status.setTextColor(
            if (complaint.status == "Pending") android.graphics.Color.RED
            else android.graphics.Color.GREEN
        )
    }

    override fun getItemCount() = complaints.size
}
