package com.arafatporosh.hallmanagement

data class Complaints(
    val id: String? = null,
    val studentID: String = "",
    val heading: String = "",
    val details: String = "",
    val type: String = "",
    val timestamp: Long = 0L,
    var status: String = ""

)