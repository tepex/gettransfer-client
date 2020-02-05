package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.domain.model.Result

interface ChatRepository {
    suspend fun getChatRemote(transferId: Long): Result<Chat>
    suspend fun getChatCached(transferId: Long): Result<Chat>

    fun onJoinRoom(transferId: Long)
    fun onLeaveRoom(transferId: Long)

    fun onSendMessage(message: Message): Result<Unit>
    fun onReadMessage(transferId: Long, messageId: Long)
}
