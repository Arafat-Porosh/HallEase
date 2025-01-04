package com.arafatporosh.hallmanagement.Roominfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arafatporosh.hallmanagement.R
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat

class RoomAdapter(
    private val roomList: MutableList<String>,
    private val onRoomClick: (String) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRoom: TextView = itemView.findViewById(R.id.tv_room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val roomName = roomList[position]
        val fullText = "Room no\n$roomName\nClick here"

        val spannableString = SpannableString(fullText)
        val redColor = ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark)

        spannableString.setSpan(
            ForegroundColorSpan(redColor),
            fullText.indexOf("Click here"),
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        holder.tvRoom.text = spannableString

        holder.itemView.setOnClickListener {
            onRoomClick(roomName)
        }
    }

    override fun getItemCount(): Int = roomList.size
}
