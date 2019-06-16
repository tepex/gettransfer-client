package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity

import com.kg.gettransfer.remote.model.ChatAccountModel
import com.kg.gettransfer.remote.model.ChatModel

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class ChatMapper : KoinComponent {
    private val chatAccountMapper = get<ChatAccountMapper>()
    private val messageMapper = get<MessageMapper>()

    fun fromRemote(type: ChatModel) =
        ChatEntity(
            accounts  = type.accounts.mapValues { chatAccountMapper.fromRemote(it.value) },
            accountId = type.accountId,
            messages  = type.messages.map { messageMapper.fromRemote(it) }.toMutableList()
        )
}

open class ChatAccountMapper {
    fun fromRemote(type: ChatAccountModel) =
        ChatAccountEntity(
            email    = type.email,
            fullName = type.fullName,
            roles    = type.roles
        )
}
