package com.example.safetyandcheap.service.dto.property

import com.example.safetyandcheap.service.dto.InfrastructureType
import com.google.gson.annotations.SerializedName

sealed class Property(
    open val type: String,
    @SerializedName("skip")
    open val id: Long,
    @SerializedName("skip")
    open val latitude: Double,
    @SerializedName("skip")
    open val longitude: Double,
    @SerializedName("skip")
    open val address: String,
    @SerializedName("skip")
    open val area: Double,
    @SerializedName("skip")
    open val bargain: Boolean,
    @SerializedName("skip")
    open val floors: Int,
    @SerializedName("skip")
    open val photos: List<Image>,
    @SerializedName("skip")
    open val infrastructure: List<InfrastructureType>
)