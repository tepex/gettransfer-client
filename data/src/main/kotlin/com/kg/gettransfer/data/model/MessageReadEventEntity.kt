package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReadEventEntity(
    @SerialName(MESSAGE_ID) val messageId: Long
) {

    companion object {
        const val ENTITY_NAME = "message_read_event"
        const val MESSAGE_ID  = "message_id"
    }
}
