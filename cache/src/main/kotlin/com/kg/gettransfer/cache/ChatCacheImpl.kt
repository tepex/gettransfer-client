package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.cache.model.mapNew
import com.kg.gettransfer.data.ChatCache
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ChatCacheImpl : ChatCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun getChat(transferId: Long): ChatEntity? {
        val chatCached = db.chatCacheDao().getChat(transferId)
        val messagesCached = db.chatCacheDao().getMessages(transferId) ?: emptyList()
        val newMessagesCached = db.chatCacheDao().getNewMessagesForTransfer(transferId) ?: emptyList()
        return chatCached?.map(messagesCached, newMessagesCached)
    }

    override fun getMessage(messageId: Long) = db.chatCacheDao().getMessage(messageId)?.map()

    override fun setChat(transferId: Long, chat: ChatEntity) {
        db.chatCacheDao().insertChat(chat.map(transferId))
        db.chatCacheDao().insertMessages(chat.messages.map { it.map() })
    }

    override fun setMessage(message: MessageEntity) = db.chatCacheDao().insertMessage(message.map())

    override fun setMessages(messages: List<MessageEntity>) =
        db.chatCacheDao().insertMessages(messages.map { it.map() })

    override fun getNewMessagesForTransfer(transferId: Long) =
        db.chatCacheDao().getNewMessagesForTransfer(transferId)?.map { it.map() } ?: emptyList()

    override fun getAllNewMessages() = db.chatCacheDao().getAllNewMessages()?.map { it.map() } ?: emptyList()

    override fun setNewMessage(newMessage: MessageEntity) = db.chatCacheDao().insertNewMessage(newMessage.mapNew())

    override fun deleteNewMessage(messageId: Long) = db.chatCacheDao().deleteNewMessage(messageId)
}
