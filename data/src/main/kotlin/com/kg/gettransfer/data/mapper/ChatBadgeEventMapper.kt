package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import java.lang.UnsupportedOperationException

class ChatBadgeEventMapper : Mapper<ChatBadgeEventEntity, ChatBadgeEvent> {
    override fun fromEntity(type: ChatBadgeEventEntity) =
            ChatBadgeEvent(
                    type.transferId,
                    type.clearBadge
            )

    override fun toEntity(type: ChatBadgeEvent): ChatBadgeEventEntity {
        throw UnsupportedOperationException()
    }
}