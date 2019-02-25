package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import org.koin.standalone.KoinComponent

interface ChatRemote: KoinComponent {
    suspend fun getChat(transferId: Long): ChatEntity
    suspend fun newMessage(transferId: Long, text: String): MessageEntity
    suspend fun readMessage(messageId: Long): Boolean
}