package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.ChatBadgeEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBadgeEventEntity(
    @SerialName(TRANSFER_ID) val transferId: Long,
    @SerialName(CLEAR_BADGE) val clearBadge: Boolean
) {

    companion object {
        const val ENTITY_NAME = "chat_badge_event"
        const val TRANSFER_ID = "transferId"
        const val CLEAR_BADGE = "clearBadge"
    }
}

fun ChatBadgeEventEntity.map() = ChatBadgeEvent(transferId, clearBadge)
