package com.kg.gettransfer.data.model

import java.util.Locale

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LocaleEntity(
    @SerialName(CODE) val code: String,
    @SerialName(TITLE) val title: String
) {

    companion object {
        const val CODE = "code"
        const val TITLE = "title"
    }
}

fun Locale.map() = LocaleEntity(language.toUpperCase(Locale.US), displayLanguage)
fun LocaleEntity.map() = Locale(code)
