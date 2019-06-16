package com.kg.gettransfer.domain.model

import java.util.Date

data class Chat(
    val accounts: Map<Long, ChatAccount>,
    val accountId: Long,
    val messages: List<Message>
)

data class ChatAccount(
    val email: String,
    val fullName: String?,
    val roles: List<String>
)

data class Message(
    override val id: Long,
    val accountId: Long,
    val transferId: Long,
    val createdAt: Date,
    val readAt: Date?,
    val text: String,
    val sendAt: Long? = null
) : Entity()
