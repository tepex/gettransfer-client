package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity

import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.ChatAccount

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class ChatMapper : KoinComponent {
    private val chatAccountMapper = get<ChatAccountMapper>()
    private val messageMapper = get<MessageMapper>()

    fun fromEntity(type: ChatEntity) =
        Chat(
            accounts = type.accounts.mapValues { chatAccountMapper.fromEntity(it.value) },
            accountId = type.accountId,
            messages = type.messages.map { messageMapper.fromEntity(it) }
        )
}

class ChatAccountMapper {
    fun fromEntity(type: ChatAccountEntity) =
        ChatAccount(
            email    = type.email,
            fullName = type.fullName,
            roles    = type.roles
        )
}
