package com.kg.gettransfer.presentation.model

data class ChatModel(
        val accounts: Map<Long, ChatAccountModel>,
        val currentAccountId: Long,
        var messages: List<MessageModel>
) {
    enum class Type {
        CURRENT_ACCOUNT_MESSAGE,
        NOT_CURRENT_ACCOUNT_MESSAGE
    }

    fun getMessageType(messagePosition: Int) = when (currentAccountId) {
        messages[messagePosition].accountId -> Type.CURRENT_ACCOUNT_MESSAGE
        else -> Type.NOT_CURRENT_ACCOUNT_MESSAGE
    }
}

data class ChatAccountModel(
        val email: String,
        val fullName: String?,
        val roles: List<String>
)