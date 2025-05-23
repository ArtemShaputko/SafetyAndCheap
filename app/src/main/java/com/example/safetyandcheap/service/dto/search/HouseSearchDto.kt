package com.example.safetyandcheap.service.dto.search

import com.example.safetyandcheap.service.dto.InfrastructureType

data class HouseSearchDto(
    override val minArea: Double? = null,
    override val maxArea: Double? = null,
    override val minFloors: Int? = null,
    override val maxFloors: Int? = null,
    override val infrastructure: List<InfrastructureType>? = null,
    val earthStatuses: List<String>? = null,
    val minSiteArea: Double? = null,
    val maxSiteArea: Double? = null,
    val minBedrooms: Int? = null,
    val maxBedrooms: Int? = null
) : PropertySearchDto("House", minArea, maxArea, minFloors, maxFloors, infrastructure)