package com.project.messmanagement

data class Member(
    val name: String,
    val initials: String,
    val room: String,
    val phone: String,
    val meals: Int,
    val due: String,
    val status: String
)
