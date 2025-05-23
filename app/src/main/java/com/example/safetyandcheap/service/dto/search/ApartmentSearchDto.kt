package com.example.safetyandcheap.service.dto.search

import com.example.safetyandcheap.service.dto.InfrastructureType

data class ApartmentSearchDto(
    override val minArea: Double? = null,
    override val maxArea: Double? = null,
    override val minFloors: Int? = null,
    override val maxFloors: Int? = null,
    override val infrastructure: List<InfrastructureType>? = null,
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val minFloor: Int? = null,
    val maxFloor: Int? = null,
    val minLivingArea: Double? = null,
    val maxLivingArea: Double? = null
) : PropertySearchDto("Apartment", minArea, maxArea, minFloors, maxFloors, infrastructure)