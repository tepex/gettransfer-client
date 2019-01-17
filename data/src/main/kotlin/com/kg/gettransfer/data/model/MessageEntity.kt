package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageEntity(
        @SerialName(ID)          val id: Long,
        @SerialName(ACCOUNT_ID)  val accountId: Long,
        @SerialName(TRANSFER_ID) val transferId: Long,
        @SerialName(CREATED_AT)  val createdAt: String,
        @SerialName(READ_AT)     val readAt: String?,
        @SerialName(TEXT)        val text: String
) {
    companion object {
        const val ENTITY_NAME = "message"
        const val ID          = "id"
        const val ACCOUNT_ID  = "account_id"
        const val TRANSFER_ID = "transfer_id"
        const val CREATED_AT  = "created_at"
        const val READ_AT     = "read_at"
        const val TEXT        = "text"
    }
}