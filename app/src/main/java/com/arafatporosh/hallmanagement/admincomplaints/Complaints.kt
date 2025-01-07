package com.arafatporosh.hallmanagement.admincomplaints

data class Complaints(
    val id: String? = "",
    val heading: String = "",
    val details: String = "",
    val timestamp: Long = 0L,
    var status: String = ""
)
