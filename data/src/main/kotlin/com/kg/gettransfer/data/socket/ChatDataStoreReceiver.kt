package com.kg.gettransfer.data.socket

import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.MessageEntity

interface ChatDataStoreReceiver {

    fun onNewMessage(message: MessageEntity)

    fun onMessageRead(messageId: Long)

    fun onChatBadgeChanged(chatBadgeEvent: ChatBadgeEventEntity)
}
