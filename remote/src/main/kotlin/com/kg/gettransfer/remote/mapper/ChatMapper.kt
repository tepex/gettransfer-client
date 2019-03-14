package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.remote.model.ChatAccountModel
import com.kg.gettransfer.remote.model.ChatModel
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

open class ChatMapper : EntityMapper<ChatModel, ChatEntity> {
    private val chatAccountMapper = get<ChatAccountMapper>()
    private val messageMapper = get<MessageMapper>()

    override fun fromRemote(type: ChatModel) =
            ChatEntity(
                    accounts         = type.accounts.mapValues { chatAccountMapper.fromRemote(it.value) },
                    currentAccountId = type.currentAccountId,
                    messages         = type.messages.map { messageMapper.fromRemote(it) }
            )

    override fun toRemote(type: ChatEntity): ChatModel {
        throw UnsupportedOperationException()
    }
}

open class ChatAccountMapper : EntityMapper<ChatAccountModel, ChatAccountEntity> {
    override fun fromRemote(type: ChatAccountModel) =
            ChatAccountEntity(
                    email    = type.email,
                    fullName = type.fullName,
                    roles    = type.roles
            )

    override fun toRemote(type: ChatAccountEntity): ChatAccountModel {
        throw UnsupportedOperationException()
    }
}