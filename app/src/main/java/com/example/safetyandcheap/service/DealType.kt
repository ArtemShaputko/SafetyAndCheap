package com.example.safetyandcheap.service

enum class DealType(
    val localName: String
) {
    Sale("Sale");

    companion object {
        fun getByLocalName(localName: String?): PropertyType? {
            return PropertyType.entries.find { it.localName.equals(localName, ignoreCase = false) }
        }
    }
}