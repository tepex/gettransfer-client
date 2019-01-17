package com.kg.gettransfer.cache.mapper

import com.kg.gettransfer.cache.model.ChatCached
import com.kg.gettransfer.cache.model.MessageCached
import com.kg.gettransfer.cache.model.ChatAccountsCachedMap
import com.kg.gettransfer.cache.model.ChatAccountCached
import com.kg.gettransfer.cache.model.StringList

import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

open class ChatEntityMapper : KoinComponent {
    private val chatAccountMapper = get<ChatAccountEntityMapper>()
    private val messageMapper = get<MessageEntityMapper>()

    fun fromCached(type: ChatCached, messages: List<MessageCached>) =
            ChatEntity(
                    accounts         = type.accounts.map.mapValues { chatAccountMapper.fromCached(it.value) },
                    currentAccountId = type.currentAccountId,
                    messages         = messages.map { messageMapper.fromCached(it) }
            )

    fun toCached(type: ChatEntity, transferId: Long) =
            ChatCached(
                    transferId       = transferId,
                    accounts         = ChatAccountsCachedMap(type.accounts.mapValues { chatAccountMapper.toCached(it.value) }),
                    currentAccountId = type.currentAccountId
            )
}

open class ChatAccountEntityMapper : EntityMapper<ChatAccountCached, ChatAccountEntity> {

    override fun fromCached(type: ChatAccountCached) =
            ChatAccountEntity(
                    email    = type.email,
                    fullName = type.fullName,
                    roles    = type.roles.list
            )

    override fun toCached(type: ChatAccountEntity) =
            ChatAccountCached(
                    email    = type.email,
                    fullName = type.fullName,
                    roles    = StringList(type.roles)
            )
}