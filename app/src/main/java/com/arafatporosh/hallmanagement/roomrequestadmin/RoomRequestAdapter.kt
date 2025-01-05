package com.arafatporosh.hallmanagement.roomrequestadmin


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.R

class RoomRequestAdapter(
    private val requestList: MutableList<RoomRequest>,
    private val onAccept: (RoomRequest) -> Unit,
    private val onReject: (RoomRequest) -> Unit
) : RecyclerView.Adapter<RoomRequestAdapter.RoomRequestViewHolder>() {

    inner class RoomRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoomNumber: TextView = itemView.findViewById(R.id.tv_room_number)
        val tvStudentIds: TextView = itemView.findViewById(R.id.tv_student_ids)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val btnAccept: Button = itemView.findViewById(R.id.btn_accept)
        val btnReject: Button = itemView.findViewById(R.id.btn_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_request, parent, false)
        return RoomRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomRequestViewHolder, position: Int) {
        val request = requestList[position]

        holder.tvRoomNumber.text = "Room No: ${request.room}"
        holder.tvStudentIds.text = request.students.joinToString("\n") { "ID: $it" }
        holder.tvStatus.text = request.status

        when (request.status) {
            "Pending" -> {
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnReject.visibility = View.VISIBLE
            }
            else -> {
                holder.tvStatus.setTextColor(holder.itemView.context.getColor(android.R.color.black))
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
        }

        holder.btnAccept.setOnClickListener {
            onAccept(request)
        }

        holder.btnReject.setOnClickListener {
            onReject(request)
        }
    }

    override fun getItemCount(): Int = requestList.size
}
