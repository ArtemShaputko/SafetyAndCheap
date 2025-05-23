package com.example.safetyandcheap.data.adapter

import com.example.safetyandcheap.service.dto.property.Apartment
import com.example.safetyandcheap.service.dto.property.House
import com.example.safetyandcheap.service.dto.property.Property
import com.example.safetyandcheap.service.dto.property.Room
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class PropertyTypeAdapter : JsonDeserializer<Property> {

    // Десериализация (JSON → Объект)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Property {
        val jsonObject = json.asJsonObject
        val typeElement = jsonObject.get("type")
            ?: throw JsonParseException("Missing 'type' field in Property JSON")

        return when (val type = typeElement.asString) {
            "Apartment" -> context.deserialize(json, Apartment::class.java)
            "House" -> context.deserialize(json, House::class.java)
            "Room" -> context.deserialize(json, Room::class.java)
            else -> throw JsonParseException("Unknown Property type: $type")
        }
    }

}