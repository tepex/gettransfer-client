package com.kg.gettransfer.remote

import com.kg.gettransfer.data.ChatRemote
import com.kg.gettransfer.data.model.ChatEntity

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.ChatModel
import com.kg.gettransfer.remote.model.map

import org.koin.core.get

class ChatRemoteImpl : ChatRemote {

    private val core = get<ApiCore>()

    override suspend fun getChat(transferId: Long): ChatEntity {
        val response: ResponseModel<ChatModel> = core.tryTwice(transferId) { id -> core.api.getChat(id) }
        @Suppress("UnsafeCallOnNullableType")
        return response.data!!.map()
    }
}
