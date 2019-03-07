package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReadSocketModel(
        @SerialName(ROOM) val room: Long,
        @SerialName(MESSAGE_ID) val messageId: Long
) {
    companion object {
        const val ROOM = "room"
        const val MESSAGE_ID = "message_id"
    }
}