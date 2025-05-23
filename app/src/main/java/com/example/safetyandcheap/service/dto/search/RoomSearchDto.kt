package com.example.safetyandcheap.service.dto.search

import com.example.safetyandcheap.service.dto.InfrastructureType

data class RoomSearchDto(
    override val minArea: Double? = null,
    override val maxArea: Double? = null,
    override val minFloors: Int? = null,
    override val maxFloors: Int? = null,
    override val infrastructure: List<InfrastructureType>? = null,
    val minRoomsInApartment: Int? = null,
    val maxRoomsInApartment: Int? = null,
    val minRoomsInOffer: Int? = null,
    val maxRoomsInOffer: Int? = null,
    val minLivingArea: Double? = null,
    val maxLivingArea: Double? = null,
    val minFloor: Int? = null,
    val maxFloor: Int? = null,
) : PropertySearchDto("Room", minArea, maxArea, minFloors, maxFloors, infrastructure)