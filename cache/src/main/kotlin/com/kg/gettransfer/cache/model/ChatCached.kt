package com.kg.gettransfer.cache.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.kg.gettransfer.data.model.ChatAccountEntity
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity
import kotlinx.serialization.Serializable

@Entity(tableName = ChatEntity.ENTITY_NAME)
data class ChatCached(
        @PrimaryKey @ColumnInfo(name = MessageEntity.TRANSFER_ID) val transferId: Long,
        @ColumnInfo(name = ChatEntity.ACCOUNTS)                   val accounts: ChatAccountsCachedMap,
        @ColumnInfo(name = ChatEntity.CURRENT_ACCOUNT_ID)         val currentAccountId: Long
)

@Serializable
data class ChatAccountCached(
        @ColumnInfo(name = ChatAccountEntity.EMAIL)     val email: String,
        @ColumnInfo(name = ChatAccountEntity.FULL_NAME) val fullName: String?,
        @ColumnInfo(name = ChatAccountEntity.ROLES)     val roles: StringList
)

@Serializable
data class ChatAccountsCachedMap (val map: Map<Long, ChatAccountCached>)