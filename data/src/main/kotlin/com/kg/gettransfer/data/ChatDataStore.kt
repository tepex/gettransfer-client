package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.core.KoinComponent

interface ChatDataStore : KoinComponent {

    suspend fun getChat(transferId: Long): ChatEntity?

    fun getMessage(messageId: Long): MessageEntity?

    fun addChat(transferId: Long, chat: ChatEntity)

    fun addMessage(message: MessageEntity)

    fun getNewMessagesForTransfer(transferId: Long): List<MessageEntity>

    fun newMessageToCache(message: MessageEntity)

    fun deleteNewMessageFromCache(messageId: Long)
}
