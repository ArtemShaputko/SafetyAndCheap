package com.example.safetyandcheap.service.dto.search

import com.example.safetyandcheap.service.dto.InfrastructureType
import com.google.gson.annotations.SerializedName


sealed class PropertySearchDto(
    open val type: String? = null,
    @SerializedName("skip")
    open val minArea: Double? = null,
    @SerializedName("skip")
    open val maxArea: Double? = null,
    @SerializedName("skip")
    open val minFloors: Int? = null,
    @SerializedName("skip")
    open val maxFloors: Int? = null,
    @SerializedName("skip")
    open val infrastructure: List<InfrastructureType>? = null
)