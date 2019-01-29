package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.repository.ChatRepository

class ChatInteractor(private val repository: ChatRepository) {
    //private var chat: Chat? = null

    suspend fun getChat(transferId: Long, fromCache: Boolean = false) =
            when(fromCache){
                false -> repository.getChat(transferId)
                true -> repository.getChatCached(transferId)
            }//.apply { if(!isError()) chat = model }
    suspend fun newMessage(message: Message) = repository.newMessage(message)//.apply { if(!isError()) chat?.messages!!.toMutableList().add(model) }
    suspend fun readMessage(messageId: Long) = repository.readMessage(messageId)

    suspend fun sendAllNewMessages() = repository.sendAllNewMessages()
}