package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ChatDataStore
import com.kg.gettransfer.data.ds.ChatDataStoreCache
import com.kg.gettransfer.data.ds.ChatDataStoreIO
import com.kg.gettransfer.data.ds.ChatDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.mapper.ChatBadgeEventMapper
import com.kg.gettransfer.data.mapper.ChatMapper
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.MessageMapper
import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.domain.interactor.ChatInteractor
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.ChatRepository

import org.koin.standalone.get
import org.koin.standalone.inject
import java.util.Calendar
import java.util.Date

class ChatRepositoryImpl(
        private val factory: DataStoreFactory<ChatDataStore, ChatDataStoreCache, ChatDataStoreRemote>,
        private val chatDataStoreIO: ChatDataStoreIO
) : BaseRepository(), ChatRepository {

    private val chatMapper = get<ChatMapper>()
    private val messageMapper = get<MessageMapper>()
    private val chatBadgeEventMapper = get<ChatBadgeEventMapper>()
    private val chatReceiver: ChatInteractor by inject()

    override suspend fun getChat(transferId: Long): Result<Chat> {
        val result: ResultEntity<ChatEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().getChat(transferId)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addChat(transferId, it) }
        return getChatCached(transferId)
    }

    override suspend fun getChatCached(transferId: Long): Result<Chat> {
        val result: ResultEntity<ChatEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getChat(transferId)
        }
        return Result(result.entity?.let { chatMapper.fromEntity(it) } ?: DEFAULT_CHAT, null, result.cacheError?.let { ExceptionMapper.map(it) })
    }

    override suspend fun newMessage(message: Message): Result<Chat>{
        factory.retrieveCacheDataStore().newMessageToCache(messageMapper.toEntity(message))
        sendAllNewMessages(message.transferId)
        return getChatCached(message.transferId)
    }

    override suspend fun sendAllNewMessages(transferId: Long?): Result<Unit> {
        val newMessages =
                if(transferId != null) factory.retrieveCacheDataStore().getNewMessagesForTransfer(transferId)
                else factory.retrieveCacheDataStore().getAllNewMessages()
        for (i in 0 until newMessages.size){
            val result: ResultEntity<MessageEntity?> = retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().newMessage(newMessages[i].transferId, newMessages[i].text)
            }
            if (result.error == null){
                result.entity?.let { factory.retrieveCacheDataStore().addMessage(result.entity) }
                factory.retrieveCacheDataStore().deleteNewMessageFromCache(newMessages[i].id)
            } else {
                return Result(Unit)
            }
        }
        return Result(Unit)
    }

    override suspend fun readMessage(messageId: Long): Result<Unit> {
        factory.retrieveRemoteDataStore().readMessage(messageId)
        return Result(Unit)
    }

    override fun onJoinRoom(transferId: Long) = chatDataStoreIO.onJoinRoomEmit(transferId)

    override fun onLeaveRoom(transferId: Long) = chatDataStoreIO.onLeaveRoomEmit(transferId)

    internal fun onNewMessageEvent(message: MessageEntity) {
        factory.retrieveCacheDataStore().addMessage(message)
        chatReceiver.onNewMessageEvent(messageMapper.fromEntity(message))
    }

    internal fun onMessageReadEvent(messageId: Long){
        val message = factory.retrieveCacheDataStore().getMessage(messageId)?.let { messageMapper.fromEntity(it) }
        if(message != null) {
            message.readAt = Calendar.getInstance().time
            factory.retrieveCacheDataStore().addMessage(messageMapper.toEntity(message))
            chatReceiver.onMessageReadEvent(message)
        }
    }

    internal fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEventEntity) {
        chatReceiver.onChatBadgeChangedEvent(chatBadgeEventMapper.fromEntity(chatBadgeEvent))
    }

    companion object {
        private val DEFAULT_CHAT =
                Chat(
                        accounts         = emptyMap(),
                        currentAccountId = 0,
                        messages         = emptyList()
                )

        private val DEFAULT_MESSAGE =
                Message(
                        id         = 0,
                        accountId  = 0,
                        transferId = 0,
                        createdAt  = Date(),
                        readAt     = null,
                        text       = ""
                )
    }
}