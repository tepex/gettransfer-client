package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.repository.ChatRepositoryImpl
import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ChatSocketDataStoreInput: ChatDataStoreReceiver, KoinComponent {
    private val repository: ChatRepositoryImpl by inject()

    override fun onNewMessage(message: MessageEntity) { repository.onNewMessageEvent(message) }
    override fun onMessageRead(messageId: Long) { repository.onMessageReadEvent(messageId) }
    override fun onChatBadgeChanged(chatBadgeEvent: ChatBadgeEventEntity) { repository.onChatBadgeChangedEvent(chatBadgeEvent) }
}