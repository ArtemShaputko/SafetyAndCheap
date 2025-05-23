package com.example.safetyandcheap.service.dto

enum class InfrastructureType(val type: String) {
    LIFT("lift"),
    INTERNET("internet"),
    VIDEO_SURVEILLANCE("video surveillance"),
    INTERCOM("intercom");

    companion object {
        fun getByType(type: String?): InfrastructureType? {
            return InfrastructureType.entries.find { it.type.equals(type, ignoreCase = false) }
        }
    }
}