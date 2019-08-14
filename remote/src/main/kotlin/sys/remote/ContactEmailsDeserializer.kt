package com.kg.gettransfer.sys.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException

import java.lang.reflect.Type

class ContactEmailsDeserializer : JsonDeserializer<ContactEmailsWrapperModel> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonContacEmails: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ContactEmailsWrapperModel {
        val types = ContactEmailsWrapperModel()
        jsonContacEmails.asJsonObject.entrySet().forEach { types.add(ContactEmailModel(it.key, it.value.asString)) }
        return types
    }
}
