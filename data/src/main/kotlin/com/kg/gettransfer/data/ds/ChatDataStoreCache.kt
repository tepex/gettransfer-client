package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ChatCache
import com.kg.gettransfer.data.ChatDataStore
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.inject

class ChatDataStoreCache : ChatDataStore {

    private val cache: ChatCache by inject()

    override suspend fun getChat(transferId: Long) = cache.getChat(transferId)

    override fun getMessage(messageId: Long) = cache.getMessage(messageId)

    override fun addChat(transferId: Long, chat: ChatEntity) = cache.setChat(transferId, chat)

    override fun addMessage(message: MessageEntity) = cache.setMessage(message)

    override fun getNewMessagesForTransfer(transferId: Long) = cache.getNewMessagesForTransfer(transferId)

    override fun getAllNewMessages() = cache.getAllNewMessages()

    override fun newMessageToCache(message: MessageEntity) = cache.setNewMessage(message)

    override fun deleteNewMessageFromCache(messageId: Long) = cache.deleteNewMessage(messageId)

    override suspend fun newMessage(transferId: Long, text: String): MessageEntity {
        throw UnsupportedOperationException()
    }

    override suspend fun readMessage(messageId: Long): Boolean {
        throw UnsupportedOperationException()
    }
}
