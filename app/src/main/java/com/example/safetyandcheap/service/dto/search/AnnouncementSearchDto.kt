package com.example.safetyandcheap.service.dto.search

import com.example.safetyandcheap.service.dto.StatusType

data class AnnouncementSearchDto(
    val statuses: List<StatusType>? = null,
    val property: PropertySearchDto? = null,
    val offer: OfferSearchDto? = null
)