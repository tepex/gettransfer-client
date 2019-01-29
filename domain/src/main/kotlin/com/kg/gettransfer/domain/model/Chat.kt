package com.kg.gettransfer.domain.model

data class Chat(
        val accounts: Map<Long, ChatAccount>,
        val currentAccountId: Long,
        val messages: List<Message>
)

data class ChatAccount(
        val email: String,
        val fullName: String,
        val roles: List<String>
)