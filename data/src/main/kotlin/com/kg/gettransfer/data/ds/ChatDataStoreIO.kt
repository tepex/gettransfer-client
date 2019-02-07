package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.repository.ChatRepositoryImpl
import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.ChatEventEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ChatDataStoreIO(private val emitter: ChatEventEmitter):
        ChatDataStoreReceiver,
        KoinComponent {
    private val repository: ChatRepositoryImpl by inject()

    override fun onNewMessage(message: MessageEntity) { repository.onNewMessageEvent(message) }

    fun onJoinRoomEmit(transferId: Long) = emitter.onJoinRoomEmit(transferId)

    fun onLeaveRoomEmit(transferId: Long) = emitter.onLeaveRoomEmit(transferId)

    override fun onMessageRead(messageId: Long) { repository.onMessageReadEvent(messageId) }

    override fun onChatBadgeChanged(chatBadgeEvent: ChatBadgeEventEntity) { repository.onChatBadgeChangedEvent(chatBadgeEvent) }

    fun onMessageEmit() {}
}