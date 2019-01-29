package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.NewMessageCached
import com.kg.gettransfer.data.model.MessageEntity

open class NewMessageEntityMapper : EntityMapper<NewMessageCached, MessageEntity> {

    override fun fromCached(type: NewMessageCached) =
            MessageEntity(
                    id         = type.id!!,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )

    override fun toCached(type: MessageEntity) =
            NewMessageCached(
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )
}