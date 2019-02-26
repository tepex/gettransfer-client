package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.MessageEntity
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = MessageEntity.NEW_MESSAGE)
data class NewMessageCached(
        @PrimaryKey(autoGenerate = true)              val id: Long? = null,
        @ColumnInfo(name = MessageEntity.ACCOUNT_ID)  val accountId: Long,
        @ColumnInfo(name = MessageEntity.TRANSFER_ID) val transferId: Long,
        @ColumnInfo(name = MessageEntity.CREATED_AT)  val createdAt: String,
        @ColumnInfo(name = MessageEntity.READ_AT)     val readAt: String?,
        @ColumnInfo(name = MessageEntity.TEXT)        val text: String,
        @ColumnInfo(name = MessageEntity.SEND_AT)     val sendAt: Long? = null
)