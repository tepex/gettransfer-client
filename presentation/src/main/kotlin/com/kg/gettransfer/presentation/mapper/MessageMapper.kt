package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.presentation.model.MessageModel

open class MessageMapper : Mapper<MessageModel, Message> {

    override fun toView(type: Message) =
            MessageModel(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )

    override fun fromView(type: MessageModel) =
            Message(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text,
                    sendAt     = type.sendAt
            )
}