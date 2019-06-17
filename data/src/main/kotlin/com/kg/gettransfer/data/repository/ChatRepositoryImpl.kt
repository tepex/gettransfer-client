package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ChatDataStore

import com.kg.gettransfer.data.ds.ChatDataStoreCache
import com.kg.gettransfer.data.ds.ChatDataStoreRemote
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.io.ChatSocketDataStoreOutput

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
import com.kg.gettransfer.domain.model.ChatAccount
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.repository.ChatRepository

import org.koin.standalone.get
import org.koin.standalone.inject

import java.util.Calendar
import java.util.Date

class ChatRepositoryImpl(
    private val factory: DataStoreFactory<ChatDataStore, ChatDataStoreCache, ChatDataStoreRemote>,
    private val chatDataStoreIO: ChatSocketDataStoreOutput
) : BaseRepository(), ChatRepository {

    private val chatMapper = get<ChatMapper>()
    private val messageMapper = get<MessageMapper>()
    private val chatBadgeEventMapper = get<ChatBadgeEventMapper>()
    private val chatReceiver: ChatInteractor by inject()

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
        
        /*
        val chat = result.entity?.let { chatMapper.fromEntity(it) } ?: DEFAULT_CHAT
        resultNewMessages.entity?.map {
            val newMessage = messageMapper.fromEntity(it)
            
            if (newMessage.accountId == DEFAULT_CHAT.accountId && chat.accountId != DEFAULT_CHAT.accountId) {
                newMessage.accountId = chat.accountId
            }
            chat.messages = chat.messages.plus(newMessage)
        }
        */
        return Result(result.entity?.let { chatMapper.fromEntity(it) } ?: DEFAULT_CHAT, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun getChatCached(transferId: Long): Result<Chat> {
        val result: ResultEntity<ChatEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getChat(transferId)
        }
        return Result(result.entity?.let { chatMapper.fromEntity(it) } ?: DEFAULT_CHAT, null,
                result.entity != null, result.cacheError?.let { ExceptionMapper.map(it) })
    }

    override suspend fun newMessage(message: Message): Result<Chat>{
        factory.retrieveCacheDataStore().newMessageToCache(messageMapper.toEntity(message))
        sendAllNewMessages(message.transferId)
        return getChatCached(message.transferId)
    }

    override suspend fun sendAllNewMessages(transferId: Long?): Result<Int> {
        val newMessages =
                if(transferId != null) factory.retrieveCacheDataStore().getNewMessagesForTransfer(transferId)
                else factory.retrieveCacheDataStore().getAllNewMessages()
        var sendedMessagesCount = 0
        for (i in 0 until newMessages.size){
            val result: ResultEntity<MessageEntity?> = retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().newMessage(newMessages[i].transferId, newMessages[i].text)
            }
            if (result.error == null){
                sendedMessagesCount++
                result.entity?.let { factory.retrieveCacheDataStore().addMessage(result.entity) }
                factory.retrieveCacheDataStore().deleteNewMessageFromCache(newMessages[i].id)
            } else {
                return Result(sendedMessagesCount)
            }
        }
        return Result(sendedMessagesCount)
    }

    override fun sendMessageFromQueue(transferId: Long): Result<Unit> {
        val newMessages = factory.retrieveCacheDataStore().getNewMessagesForTransfer(transferId)
        newMessages.firstOrNull()?.let { chatDataStoreIO.onSendMessageEmit(it.transferId, it.text) }
        return Result(Unit)
    }

    override fun onJoinRoom(transferId: Long) = chatDataStoreIO.onJoinRoomEmit(transferId)
    override fun onLeaveRoom(transferId: Long) = chatDataStoreIO.onLeaveRoomEmit(transferId)
    override fun onSendMessage(message: Message): Result<Unit> {
        factory.retrieveCacheDataStore().newMessageToCache(messageMapper.toEntity(message))
        sendMessageFromQueue(message.transferId)
        return Result(Unit)
    }
    override fun onReadMessage(transferId: Long, messageId: Long) = chatDataStoreIO.onReadMessageEmit(transferId, messageId)

    internal fun onNewMessageEvent(message: MessageEntity) {
        factory.retrieveCacheDataStore().addMessage(message)
        val newMessages = factory.retrieveCacheDataStore().getNewMessagesForTransfer(message.transferId)
        val messageFromCache = newMessages.find {
            (it.accountId == message.accountId || it.accountId == DEFAULT_MESSAGE.accountId) && it.text == message.text
        }
        messageFromCache?.let { factory.retrieveCacheDataStore().deleteNewMessageFromCache(it.id) }
        sendMessageFromQueue(message.transferId)
        chatReceiver.onNewMessageEvent(messageMapper.fromEntity(message))
    }

    internal fun onMessageReadEvent(messageId: Long){
        factory.retrieveCacheDataStore().getMessage(messageId)?.let { messageMapper.fromEntity(it) }?.let {
            val message = it.copy(readAt = Calendar.getInstance().time)
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
                accounts  = emptyMap<Long, ChatAccount>(),
                accountId = 0,
                messages  = emptyList<Message>().toMutableList()
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
