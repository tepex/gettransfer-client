package com.kg.gettransfer.data.model

import com.google.gson.JsonObject
import com.kg.gettransfer.domain.model.Token

sealed class TokenEntity<String, JsonObject>

class StringTokenEntity<String, JsonObject>(val token: String) : TokenEntity<String, JsonObject>()

class JsonTokenEntity<String, JsonObject>(val token: JsonObject) : TokenEntity<String, JsonObject>()

fun Token<String, JsonObject>.map(): TokenEntity<String, JsonObject> = when (this) {
    is Token.StringToken -> StringTokenEntity(token)
    is Token.JsonToken   -> JsonTokenEntity(token)
}
