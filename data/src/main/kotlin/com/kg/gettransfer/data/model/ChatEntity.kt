package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.ChatAccount
import java.text.DateFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatEntity(
    @SerialName(ACCOUNTS)   val accounts: Map<Long, ChatAccountEntity>,
    @SerialName(ACCOUNT_ID) val accountId: Long,
    @SerialName(MESSAGES)   val messages: MutableList<MessageEntity>
) {
    companion object {
        const val ENTITY_NAME = "chat"
        const val ACCOUNTS    = "accounts"
        const val ACCOUNT_ID  = "currentAccountId"
        const val MESSAGES    = "messages"
    }
}

@Serializable
data class ChatAccountEntity(
    @SerialName(EMAIL)     val email: String,
    @SerialName(FULL_NAME) val fullName: String?,
    @SerialName(ROLES)     val roles: List<String>
) {
    companion object {
        const val EMAIL     = "email"
        const val FULL_NAME = "full_name"
        const val ROLES     = "roles"
    }
}

fun ChatAccountEntity.map() = ChatAccount(email, fullName, roles)

fun ChatEntity.map(dateFormat: DateFormat) =
    Chat(
        accounts.mapValues { it.value.map() },
        accountId,
        messages.map { it.map(dateFormat) }
    )
