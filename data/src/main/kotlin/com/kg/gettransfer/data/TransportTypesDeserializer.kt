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
	override fun deserialize(jsonTransportTypes: JsonElement,
	                         typeOfT: Type,
	                         context: JsonDeserializationContext): ApiTransportTypesWrapper {
		val types = ApiTransportTypesWrapper()
		/* jsonTransportTypes is natively object with fields of type `ApiTransportType`,
		   but we need a List of this fields. */
		jsonTransportTypes.asJsonObject.entrySet().forEach {
		    types.add(context.deserialize(it.value.asJsonObject, ApiTransportType::class.java))
		}
		return types
	}
}
