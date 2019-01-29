package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message

interface ChatRepository {
    suspend fun getChat(transferId: Long): Result<Chat>
    suspend fun getChatCached(transferId: Long): Result<Chat>
    suspend fun newMessage(message: Message): Result<Chat>
    suspend fun sendAllNewMessages(transferId: Long? = null): Result<Unit>
    suspend fun readMessage(messageId: Long): Result<Unit>
}