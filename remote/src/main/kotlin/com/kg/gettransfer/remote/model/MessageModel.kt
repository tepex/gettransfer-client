package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.MessageEntity

class MessageWrapperModel(@SerializedName(MessageEntity.ENTITY_NAME) @Expose val message: MessageModel)

class MessageModel(
    @SerializedName(MessageEntity.ID)          @Expose val id: Long,
    @SerializedName(MessageEntity.ACCOUNT_ID)  @Expose val accountId: Long,
    @SerializedName(MessageEntity.TRANSFER_ID) @Expose val transferId: Long,
    @SerializedName(MessageEntity.CREATED_AT)  @Expose val createdAt: String,
    @SerializedName(MessageEntity.READ_AT)     @Expose val readAt: String?,
    @SerializedName(MessageEntity.TEXT)        @Expose val text: String
)

fun MessageModel.map() =
    MessageEntity(
        id,
        accountId,
        transferId,
        createdAt,
        readAt,
        text
    )
