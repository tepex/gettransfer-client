package com.kg.gettransfer.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.kg.gettransfer.remote.model.TransportTypeModel
import com.kg.gettransfer.remote.model.TransportTypesWrapperModel
import java.lang.reflect.Type

class TransportTypesDeserializer : JsonDeserializer<TransportTypesWrapperModel> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonTransportTypes: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): TransportTypesWrapperModel {
        val types = TransportTypesWrapperModel()
        /* jsonTransportTypes is natively object with fields of type `TransportTypeModel`,
           but we need a List of this fields. */
        jsonTransportTypes.asJsonObject.entrySet().forEach {
            types.add(context.deserialize(it.value.asJsonObject, TransportTypeModel::class.java))
        }
        return types
    }
}
