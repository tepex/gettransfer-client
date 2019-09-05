package com.kg.gettransfer.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kg.gettransfer.data.model.MessageEntity

import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = MessageEntity.NEW_MESSAGE)
data class NewMessageCached(
    @PrimaryKey(autoGenerate = true)              val id: Long = 0L,
    @ColumnInfo(name = MessageEntity.ACCOUNT_ID)  val accountId: Long,
    @ColumnInfo(name = MessageEntity.TRANSFER_ID) val transferId: Long,
    @ColumnInfo(name = MessageEntity.CREATED_AT)  val createdAt: String,
    @ColumnInfo(name = MessageEntity.READ_AT)     val readAt: String?,
    @ColumnInfo(name = MessageEntity.TEXT)        val text: String,
    @ColumnInfo(name = MessageEntity.SEND_AT)     val sendAt: Long? = null
)

fun NewMessageCached.map() =
    MessageEntity(
        id,
        accountId,
        transferId,
        createdAt,
        readAt,
        text,
        sendAt
    )

fun MessageEntity.mapNew() =
    NewMessageCached(
        0L,
        accountId,
        transferId,
        createdAt,
        readAt,
        text,
        sendAt
    )
