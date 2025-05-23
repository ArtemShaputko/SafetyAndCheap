package com.example.safetyandcheap.service.dto

data class User(
    val id: Long,
    val name: String,
    val surname: String?,
    val email: String,
    val phoneNumber: String?
)
