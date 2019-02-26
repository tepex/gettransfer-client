package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity

data class ChatModel(
        @SerializedName(ChatEntity.ACCOUNTS) @Expose           val accounts: Map<Long, ChatAccountModel>,
        @SerializedName(ChatEntity.CURRENT_ACCOUNT_ID) @Expose val currentAccountId: Long,
        @SerializedName(ChatEntity.MESSAGES) @Expose           val messages: List<MessageModel>
)

data class ChatAccountModel(
        @SerializedName(ChatAccountEntity.EMAIL) @Expose     val email: String,
        @SerializedName(ChatAccountEntity.FULL_NAME) @Expose val fullName: String,
        @SerializedName(ChatAccountEntity.ROLES) @Expose     val roles: List<String>
)