@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ChatDataStore
import com.kg.gettransfer.data.ds.ChatDataStoreCache
import com.kg.gettransfer.data.ds.ChatDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.io.ChatSocketDataStoreOutput
import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.interactor.ChatInteractor
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.ChatRepository
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named
import java.text.DateFormat
import java.util.Calendar

class ChatRepositoryImpl(
    private val factory: DataStoreFactory<ChatDataStore, ChatDataStoreCache, ChatDataStoreRemote>,
    private val chatDataStoreIO: ChatSocketDataStoreOutput
) : BaseRepository(), ChatRepository {

    private val chatReceiver: ChatInteractor by inject()
    private val dateFormat = get<ThreadLocal<DateFormat>>(named("iso_date"))

    override suspend fun getChatRemote(transferId: Long): Result<Chat> {
        val result: ResultEntity<ChatEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getChat(transferId)
        }
        val resultNewMessages: ResultEntity<List<MessageEntity>?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getNewMessagesForTransfer(transferId)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addChat(transferId, it) }
        resultNewMessages.entity?.let {
            result.entity?.messages?.addAll(it)
        }
        return Result(
            result.entity?.map(dateFormat.get()) ?: Chat.EMPTY,
            result.error?.map()
        )
    }

    override suspend fun getChatCached(transferId: Long): Result<Chat> {
        val result: ResultEntity<ChatEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getChat(transferId)
        }
        return Result(
            result.entity?.map(dateFormat.get()) ?: Chat.EMPTY,
            null,
            result.entity != null,
            result.cacheError?.map()
        )
    }

    private fun sendMessageFromQueue(transferId: Long): Result<Unit> {
        val newMessages = factory.retrieveCacheDataStore().getNewMessagesForTransfer(transferId)
        //factory.retrieveCacheDataStore().getAllNewMessages()
        newMessages.firstOrNull()?.let { chatDataStoreIO.onSendMessageEmit(it.transferId, it.text) }
        return Result(Unit)
    }

    override fun onJoinRoom(transferId: Long) = chatDataStoreIO.onJoinRoomEmit(transferId)

    override fun onLeaveRoom(transferId: Long) = chatDataStoreIO.onLeaveRoomEmit(transferId)

    override fun onSendMessage(message: Message): Result<Unit> {
        factory.retrieveCacheDataStore().newMessageToCache(message.map(dateFormat.get()))
        sendMessageFromQueue(message.transferId)
        return Result(Unit)
    }

    override fun onReadMessage(transferId: Long, messageId: Long) =
        chatDataStoreIO.onReadMessageEmit(transferId, messageId)

    internal fun onNewMessageEvent(message: MessageEntity) {
        factory.retrieveCacheDataStore().addMessage(message)
        val newMessages = factory.retrieveCacheDataStore().getNewMessagesForTransfer(message.transferId)
        val messageFromCache = newMessages.find {
            (it.accountId == message.accountId || it.accountId == Message.EMPTY.accountId) && it.text == message.text
        }
        messageFromCache?.let { factory.retrieveCacheDataStore().deleteNewMessageFromCache(it.id) }
        sendMessageFromQueue(message.transferId)
        chatReceiver.onNewMessageEvent(message.map(dateFormat.get()))
    }

    internal fun onMessageReadEvent(messageId: Long) {
        factory.retrieveCacheDataStore().getMessage(messageId)?.map(dateFormat.get())?.let { msg ->
            val message = msg.copy(readAt = Calendar.getInstance().time)
            factory.retrieveCacheDataStore().addMessage(message.map(dateFormat.get()))
            chatReceiver.onMessageReadEvent(message)
        }
    }

    internal fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEventEntity) {
        chatReceiver.onChatBadgeChangedEvent(chatBadgeEvent.map())
    }
}
