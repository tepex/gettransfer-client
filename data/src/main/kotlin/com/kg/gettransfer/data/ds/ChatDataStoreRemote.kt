package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ChatDataStore
import com.kg.gettransfer.data.ChatRemote
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.core.inject

class ChatDataStoreRemote : ChatDataStore {

    private val remote: ChatRemote by inject()

    override suspend fun getChat(transferId: Long) = remote.getChat(transferId)

    override fun addChat(transferId: Long, chat: ChatEntity) {
        throw UnsupportedOperationException()
    }

    override fun addMessage(message: MessageEntity) {
        throw UnsupportedOperationException()
    }

    override fun getNewMessagesForTransfer(transferId: Long): List<MessageEntity> {
        throw UnsupportedOperationException()
    }

    override fun getAllNewMessages(): List<MessageEntity> {
        throw UnsupportedOperationException()
    }

    override fun newMessageToCache(message: MessageEntity) {
        throw UnsupportedOperationException()
    }

    override fun deleteNewMessageFromCache(messageId: Long) {
        throw UnsupportedOperationException()
    }

    override fun getMessage(messageId: Long): MessageEntity? {
        throw UnsupportedOperationException()
    }
}
