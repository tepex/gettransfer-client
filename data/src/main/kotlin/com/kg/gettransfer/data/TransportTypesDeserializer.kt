package com.kg.gettransfer.data

import java.lang.reflect.Type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException

import com.kg.gettransfer.data.model.ApiTransportType

import timber.log.Timber

class TransportTypesDeserializer: JsonDeserializer<Map<String, ApiTransportType>> {
	@Throws(JsonParseException::class)
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): 
		Map<String, ApiTransportType>? {
		
		val map = HashMap<String, ApiTransportType>()
		json.asJsonObject.entrySet().forEach { 
			map.put(it.key, context.deserialize(it.value.asJsonObject, ApiTransportType::class.java))
		}
		return map
	}
}
