package com.example.safetyandcheap.service

enum class PropertyType(
    val localName: String
) {
    House("House"),
    Apartment("Apartment"),
    Room("Room");

    companion object {
        fun getByLocalName(localName: String?): PropertyType? {
            return entries.find { it.localName.equals(localName, ignoreCase = false) }
        }
    }
}