package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kg.gettransfer.data.model.MessageEntity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = MessageEntity.ENTITY_NAME)
data class MessageCached(
    @PrimaryKey @ColumnInfo(name = MessageEntity.ID) val id: Long,
    @ColumnInfo(name = MessageEntity.ACCOUNT_ID)     val accountId: Long,
    @ColumnInfo(name = MessageEntity.TRANSFER_ID)    val transferId: Long,
    @ColumnInfo(name = MessageEntity.CREATED_AT)     val createdAt: String,
    @ColumnInfo(name = MessageEntity.READ_AT)        val readAt: String?,
    @ColumnInfo(name = MessageEntity.TEXT)           val text: String,
    @ColumnInfo(name = MessageEntity.SEND_AT)        val sendAt: Long? = null
)

fun MessageCached.map() = MessageEntity(id, accountId, transferId, createdAt, readAt, text, sendAt)

fun MessageEntity.map() = MessageCached(id, accountId, transferId, createdAt, readAt, text, sendAt)
