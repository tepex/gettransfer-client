package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.remote.model.MessageModel
import java.lang.UnsupportedOperationException

open class MessageMapper : EntityMapper<MessageModel, MessageEntity> {
    override fun fromRemote(type: MessageModel) =
            MessageEntity(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = type.createdAt,
                    readAt     = type.readAt,
                    text       = type.text
            )

    override fun toRemote(type: MessageEntity): MessageModel {
        throw UnsupportedOperationException()
    }
}