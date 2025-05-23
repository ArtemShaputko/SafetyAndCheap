package com.example.safetyandcheap.service.dto.property

import com.example.safetyandcheap.service.dto.StatusType
import com.example.safetyandcheap.service.dto.User


data class Announcement (
    val id: Long,
    val status: StatusType,
    val publishDate: String,
    val description: String,
    val author: User,
    val property: Property,
    val offer: Offer
)