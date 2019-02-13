package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.ChatBadgeEventListener
import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.model.ChatBadgeEvent
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.repository.ChatRepository

class ChatInteractor(private val repository: ChatRepository) {
    //private var chat: Chat? = null

    var eventChatReceiver: ChatEventListener? = null
    var eventChatBadgeReceiver: ChatBadgeEventListener? = null

    suspend fun getChat(transferId: Long, fromCache: Boolean = false) =
            when(fromCache){
                false -> repository.getChatRemote(transferId)
                true -> repository.getChatCached(transferId)
            }//.apply { if(!isError()) chat = model }
    suspend fun newMessage(message: Message) = repository.newMessage(message)//.apply { if(!isError()) chat?.messages!!.toMutableList().add(model) }
    suspend fun readMessage(messageId: Long) = repository.readMessage(messageId)

    suspend fun sendAllNewMessages(transferId: Long? = null) = repository.sendAllNewMessages(transferId)

    fun onJoinRoom(transferId: Long) = repository.onJoinRoom(transferId)
    fun onNewMessageEvent(message: Message) = eventChatReceiver?.onNewMessageEvent(message)
    fun onMessageReadEvent(message: Message) = eventChatReceiver?.onMessageReadEvent(message)
    fun onLeaveRoom(transferId: Long) = repository.onLeaveRoom(transferId)

    fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent) = eventChatBadgeReceiver?.onChatBadgeChangedEvent(chatBadgeEvent)
}