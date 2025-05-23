package com.example.safetyandcheap.service.dto.property

import com.example.safetyandcheap.service.dto.InfrastructureType

// RoomDto
data class Room(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val address: String,
    override val area: Double,
    override val bargain: Boolean,
    override val floors: Int,
    override val photos: List<Image>,
    override val infrastructure: List<InfrastructureType>,
    val roomsInApartment: Int,
    val roomsInOffer: Int,
    val livingArea: Double,
    val floor: Int
) : Property(
    "Room",
    id,
    latitude,
    longitude,
    address,
    area,
    bargain,
    floors,
    photos,
    infrastructure
)