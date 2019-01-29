package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.domain.model.Message
import org.koin.standalone.get
import java.text.DateFormat

class MessageMapper : Mapper<MessageEntity, Message> {
    private val dateFormat = get<ThreadLocal<DateFormat>>("iso_date")

    override fun fromEntity(type: MessageEntity) =
            Message(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = dateFormat.get().parse(type.createdAt),
                    readAt     = type.readAt?.let { dateFormat.get().parse(it) },
                    text       = type.text
            )

    override fun toEntity(type: Message) =
            MessageEntity(
                    id         = type.id,
                    accountId  = type.accountId,
                    transferId = type.transferId,
                    createdAt  = dateFormat.get().format(type.createdAt),
                    readAt     = type.readAt?.let { dateFormat.get().format(it) },
                    text       = type.text,
                    sendAt     = type.sendAt
            )
}