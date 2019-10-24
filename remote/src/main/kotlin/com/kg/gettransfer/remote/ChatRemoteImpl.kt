package com.kg.gettransfer.remote

import com.kg.gettransfer.data.ChatRemote
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.ChatModel
import com.kg.gettransfer.remote.model.MessageWrapperModel
import com.kg.gettransfer.remote.model.MessageNewWrapperModel
import com.kg.gettransfer.remote.model.MessageNewModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class ChatRemoteImpl : ChatRemote {

    private val core = get<ApiCore>()

    override suspend fun getChat(transferId: Long): ChatEntity {
        val response: ResponseModel<ChatModel> = core.tryTwice(transferId) { id -> core.api.getChat(id) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }

    override suspend fun newMessage(transferId: Long, text: String): MessageEntity {
        val response: ResponseModel<MessageWrapperModel> = core.tryTwice(transferId) { id ->
            core.api.newMessage(id, MessageNewWrapperModel(MessageNewModel(text)))
        }
        @Suppress("UnsafeCallOnNullableType")
        return response.data?.message!!.map()
    }

    override suspend fun readMessage(messageId: Long): Boolean {
        val response: ResponseModel<MessageWrapperModel> = core.tryTwice(messageId) { id -> core.api.readMessage(id) }
        return response.result == "success"
    }
}
