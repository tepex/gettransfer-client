package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageNewSocketModel(
    @SerialName(ROOM) val room: Long,
    @SerialName(TEXT) val text: String
) {

    companion object {
        const val ROOM = "room"
        const val TEXT = "text"
    }
}
