package com.kg.gettransfer.domain.model

import com.google.gson.JsonObject

sealed class Token<String, JsonObject> {
    class JsonToken(val token: JsonObject) : Token<String, JsonObject>()
    class StringToken(val token: String) : Token<String, JsonObject>()
}
