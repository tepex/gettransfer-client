package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.MessageCached
import com.kg.gettransfer.data.model.MessageEntity

open class MessageEntityMapper : EntityMapper<MessageCached, MessageEntity> {

    override fun fromCached(type: MessageCached) =
            MessageEntity(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )

    override fun toCached(type: MessageEntity) =
            MessageCached(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )
}