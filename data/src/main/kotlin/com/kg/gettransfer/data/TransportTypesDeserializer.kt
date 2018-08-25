package com.kg.gettransfer.data

import java.lang.reflect.Type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException

import com.kg.gettransfer.data.model.ApiTransportType
import com.kg.gettransfer.data.model.ApiTransportTypesWrapper

import timber.log.Timber

class TransportTypesDeserializer: JsonDeserializer<ApiTransportTypesWrapper> {
	@Throws(JsonParseException::class)
	override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): 
		ApiTransportTypesWrapper {
		val types = ApiTransportTypesWrapper()
		json.asJsonObject.entrySet().forEach { types.add(context.deserialize(it.value.asJsonObject, ApiTransportType::class.java)) }
		return types
	}
}
