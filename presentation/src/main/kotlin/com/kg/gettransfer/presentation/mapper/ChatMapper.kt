package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.ChatAccount

import com.kg.gettransfer.presentation.model.ChatAccountModel
import com.kg.gettransfer.presentation.model.ChatModel

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class ChatMapper : KoinComponent {
    private val chatAccountMapper  = get<ChatAccountMapper>()
    private val messageMapper  = get<MessageMapper>()

    fun toView(type: Chat) =
        ChatModel(
            accounts  = type.accounts.mapValues { chatAccountMapper.toView(it.value) },
            accountId = type.accountId,
            messages  = type.messages.map { messageMapper.toView(it) }
        )
}

open class ChatAccountMapper {
    fun toView(type: ChatAccount) =
        ChatAccountModel(
            email    = type.email,
            fullName = type.fullName,
            roles    = type.roles
        )
}
