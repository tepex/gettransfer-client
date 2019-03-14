package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.ChatAccount
import com.kg.gettransfer.presentation.model.ChatAccountModel
import com.kg.gettransfer.presentation.model.ChatModel
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

open class ChatMapper : Mapper<ChatModel, Chat> {
    private val chatAccountMapper  = get<ChatAccountMapper>()
    private val messageMapper  = get<MessageMapper>()

    override fun toView(type: Chat) =
            ChatModel(
                    accounts         = type.accounts.mapValues { chatAccountMapper.toView(it.value) },
                    currentAccountId = type.currentAccountId,
                    messages         = type.messages.map { messageMapper.toView(it) }
            )

    override fun fromView(type: ChatModel): Chat {
        throw UnsupportedOperationException()
    }
}

open class ChatAccountMapper : Mapper<ChatAccountModel, ChatAccount> {
    override fun toView(type: ChatAccount) =
            ChatAccountModel(
                    email    = type.email,
                    fullName = type.fullName,
                    roles    = type.roles
            )

    override fun fromView(type: ChatAccountModel): ChatAccount {
        throw UnsupportedOperationException()
    }
}