package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.KoinComponent

interface ChatDataStore : KoinComponent {

    suspend fun getChat(transferId: Long): ChatEntity?

    fun getMessage(messageId: Long): MessageEntity?

    suspend fun newMessage(transferId: Long, text: String): MessageEntity

    suspend fun readMessage(messageId: Long): Boolean

    fun addChat(transferId: Long, chat: ChatEntity)

    fun addMessage(message: MessageEntity)

    fun getNewMessagesForTransfer(transferId: Long): List<MessageEntity>

    fun getAllNewMessages(): List<MessageEntity>

    fun newMessageToCache(message: MessageEntity)

    fun deleteNewMessageFromCache(messageId: Long)
}
