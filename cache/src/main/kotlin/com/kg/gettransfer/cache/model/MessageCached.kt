package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.MessageEntity
import kotlinx.serialization.Serializable

@Serializable
data class MessageCached(
    @PrimaryKey @ColumnInfo(name = MessageEntity.ID) val id: Long,
    @ColumnInfo(name = MessageEntity.ACCOUNT_ID)     val accountId: Long,
    @ColumnInfo(name = MessageEntity.TRANSFER_ID)    val transferId: Long,
    @ColumnInfo(name = MessageEntity.CREATED_AT)     val createdAt: String,
    @ColumnInfo(name = MessageEntity.READ_AT)        val readAt: String?,
    @ColumnInfo(name = MessageEntity.TEXT)           val text: String
)