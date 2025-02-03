package com.arafatporosh.hallmanagement.roomrequestadmin


data class RoomRequest(
    val room: String = "",
    val status: String = "",
    val students: List<String> = emptyList(),
    val key: String = "",
    val submittedBy: String = ""
)
