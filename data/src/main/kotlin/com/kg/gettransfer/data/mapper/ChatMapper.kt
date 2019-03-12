package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.ChatAccount
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

class ChatMapper : Mapper<ChatEntity, Chat> {
    private val chatAccountMapper = get<ChatAccountMapper>()
    private val messageMapper = get<MessageMapper>()

    override fun fromEntity(type: ChatEntity) =
            Chat(
                    accounts = type.accounts.mapValues { chatAccountMapper.fromEntity(it.value) },
                    currentAccountId = type.currentAccountId,
                    messages = type.messages.map { messageMapper.fromEntity(it) }
            )

    override fun toEntity(type: Chat): ChatEntity {
        throw UnsupportedOperationException()
    }
}

class ChatAccountMapper : Mapper<ChatAccountEntity, ChatAccount> {
    override fun fromEntity(type: ChatAccountEntity) =
            ChatAccount(
                    email    = type.email,
                    fullName = type.fullName,
                    roles    = type.roles
            )
    override fun toEntity(type: ChatAccount): ChatAccountEntity {
        throw UnsupportedOperationException()
    }
}