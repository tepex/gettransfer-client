package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.KoinComponent

interface ChatCache : KoinComponent {
    fun getChat(transferId: Long): ChatEntity?
    fun getMessage(messageId: Long): MessageEntity?
    fun setChat(transferId: Long, chat: ChatEntity)
    fun setMessage(message: MessageEntity)
    fun setMessages(messages: List<MessageEntity>)

    fun getNewMessagesForTransfer(transferId: Long): List<MessageEntity>
    fun getAllNewMessages(): List<MessageEntity>
    fun setNewMessage(newMessage: MessageEntity)
    fun deleteNewMessage(messageId: Long)
}