package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message

interface ChatRepository {
    suspend fun getChatRemote(transferId: Long): Result<Chat>
    suspend fun getChatCached(transferId: Long): Result<Chat>
    suspend fun newMessage(message: Message): Result<Chat>
    suspend fun sendAllNewMessages(transferId: Long? = null): Result<Int>

    fun onJoinRoom(transferId: Long)
    fun onLeaveRoom(transferId: Long)
    fun onSendMessage(message: Message): Result<Unit>
    fun onReadMessage(transferId: Long, messageId: Long)
    fun sendMessageFromQueue(transferId: Long): Result<Unit>
}