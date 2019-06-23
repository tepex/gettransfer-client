package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.repository.ChatRepository

class ChatInteractor(private val repository: ChatRepository) {

    var eventChatReceiver: ChatEventListener? = null
    var eventChatBadgeReceiver: ChatBadgeEventListener? = null

    suspend fun getChat(transferId: Long, fromCache: Boolean = false) =
        if (fromCache) repository.getChatCached(transferId) else repository.getChatRemote(transferId)

    suspend fun newMessage(message: Message) = repository.onSendMessage(message)

    fun readMessage(transferId: Long, messageId: Long) = repository.onReadMessage(transferId, messageId)

    suspend fun sendAllNewMessages(transferId: Long? = null) = repository.sendAllNewMessages(transferId)

    fun sendMessageFromQueue(transferId: Long) = repository.sendMessageFromQueue(transferId)

    fun onJoinRoom(transferId: Long) = repository.onJoinRoom(transferId)
    fun onNewMessageEvent(message: Message) = eventChatReceiver?.onNewMessageEvent(message)
    fun onMessageReadEvent(message: Message) = eventChatReceiver?.onMessageReadEvent(message)
    fun onLeaveRoom(transferId: Long) = repository.onLeaveRoom(transferId)

    fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent) =
        eventChatBadgeReceiver?.onChatBadgeChangedEvent(chatBadgeEvent)
}
