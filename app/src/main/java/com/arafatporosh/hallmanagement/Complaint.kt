package com.arafatporosh.hallmanagement

data class Complaint(
    val id: String? = null,
    val heading: String = "",
    val details: String = "",
    val timestamp: Long = 0L,
    val status: String = "Pending"
)
