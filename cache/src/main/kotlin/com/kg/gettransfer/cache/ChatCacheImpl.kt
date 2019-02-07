package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.ChatEntityMapper
import com.kg.gettransfer.cache.mapper.MessageEntityMapper
import com.kg.gettransfer.cache.mapper.NewMessageEntityMapper
import com.kg.gettransfer.data.ChatCache
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ChatCacheImpl: ChatCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val chatMapper: ChatEntityMapper by inject()
    private val messageMapper: MessageEntityMapper by inject()
    private val newMessageMapper: NewMessageEntityMapper by inject()

    override fun getChat(transferId: Long): ChatEntity?{
        val chatCached = db.chatCacheDao().getChat(transferId)
        val messagesCached = db.chatCacheDao().getMessages(transferId) ?: emptyList()
        val newMessagesCached = db.chatCacheDao().getNewMessagesForTransfer(transferId) ?: emptyList()
        return chatCached?.let { chatMapper.fromCached(it, messagesCached, newMessagesCached) }
    }
    override fun getMessage(messageId: Long) = db.chatCacheDao().getMessage(messageId)?.let { messageMapper.fromCached(it) }
    override fun setChat(transferId: Long, chat: ChatEntity) {
        db.chatCacheDao().insertChat(chatMapper.toCached(chat, transferId))
        db.chatCacheDao().insertMessages(chat.messages.map { messageMapper.toCached(it) })
    }
    override fun setMessage(message: MessageEntity) = db.chatCacheDao().insertMessage(messageMapper.toCached(message))
    override fun setMessages(messages: List<MessageEntity>) = db.chatCacheDao().insertMessages(messages.map { messageMapper.toCached(it) })


    override fun getNewMessagesForTransfer(transferId: Long) = db.chatCacheDao().getNewMessagesForTransfer(transferId)?.map { newMessageMapper.fromCached(it) } ?: emptyList()
    override fun getAllNewMessages()= db.chatCacheDao().getAllNewMessages()?.map { newMessageMapper.fromCached(it) } ?: emptyList()
    override fun setNewMessage(newMessage: MessageEntity) = db.chatCacheDao().insertNewMessage(newMessageMapper.toCached(newMessage))
    override fun deleteNewMessage(messageId: Long) = db.chatCacheDao().deleteNewMessage(messageId)
}