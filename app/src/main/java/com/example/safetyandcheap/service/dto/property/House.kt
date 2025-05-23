package com.example.safetyandcheap.service.dto.property

import com.example.safetyandcheap.service.dto.InfrastructureType

data class House(
    override val id: Long,
    override val latitude: Double,
    override val longitude: Double,
    override val address: String,
    override val area: Double,
    override val bargain: Boolean,
    override val floors: Int,
    override val photos: List<Image>,
    override val infrastructure: List<InfrastructureType>,
    val earthStatus: String,
    val siteArea: Double,
    val bedrooms: Int
) : Property("House", id, latitude, longitude, address, area, bargain, floors, photos, infrastructure)