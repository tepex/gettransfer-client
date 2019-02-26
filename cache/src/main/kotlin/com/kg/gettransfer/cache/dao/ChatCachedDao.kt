package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kg.gettransfer.cache.model.ChatCached
import com.kg.gettransfer.cache.model.MessageCached
import com.kg.gettransfer.cache.model.NewMessageCached
import com.kg.gettransfer.data.model.ChatEntity
import com.kg.gettransfer.data.model.MessageEntity

@Dao
interface ChatCachedDao {
    @Query("SELECT * FROM ${ChatEntity.ENTITY_NAME} WHERE ${MessageEntity.TRANSFER_ID} = :transferId")
    fun getChat(transferId: Long): ChatCached?

    @Query("SELECT * FROM ${MessageEntity.ENTITY_NAME} WHERE ${MessageEntity.TRANSFER_ID} = :transferId")
    fun getMessages(transferId: Long): List<MessageCached>?

    @Query("SELECT * FROM ${MessageEntity.ENTITY_NAME} WHERE ${MessageEntity.ID} = :messageId")
    fun getMessage(messageId: Long): MessageCached?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChat(chat: ChatCached)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessages(messages: List<MessageCached>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageCached)


    @Query("SELECT * FROM ${MessageEntity.NEW_MESSAGE} WHERE ${MessageEntity.TRANSFER_ID} = :transferId")
    fun getNewMessagesForTransfer(transferId: Long): List<NewMessageCached>?

    @Query("SELECT * FROM ${MessageEntity.NEW_MESSAGE}")
    fun getAllNewMessages(): List<NewMessageCached>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewMessage(newMessage: NewMessageCached)

    @Query("DELETE FROM ${MessageEntity.NEW_MESSAGE} WHERE ${MessageEntity.ID} = :messageId")
    fun deleteNewMessage(messageId: Long)
}