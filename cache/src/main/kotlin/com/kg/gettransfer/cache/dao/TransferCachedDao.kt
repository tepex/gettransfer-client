package com.kg.gettransfer.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.data.model.TransferEntity

@Dao
interface TransferCachedDao {

    @Query("""SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE ${TransferEntity.ID} = :id""")
    fun getTransfer(id: Long): TransferCached?

    @Query("""SELECT * FROM ${TransferEntity.ENTITY_NAME} ORDER BY ${TransferEntity.ID} DESC""")
    fun getAllTransfers(): List<TransferCached>

    @Query("""
        SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE
        ${TransferEntity.STATUS} = 'completed' OR
        ${TransferEntity.STATUS} = 'not_completed'
        ORDER BY ${TransferEntity.ID} DESC
    """)
    fun getTransfersCompleted(): List<TransferCached>

    @Query("""
        SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE
        ${TransferEntity.STATUS} = 'new' OR
        ${TransferEntity.STATUS} = 'draft' OR
        ${TransferEntity.STATUS} = 'performed'
        ORDER BY ${TransferEntity.ID} DESC
    """)
    fun getTransfersActive(): List<TransferCached>

    @Query("""
        SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE
        ${TransferEntity.STATUS} = 'completed' OR
        ${TransferEntity.STATUS} = 'canceled' OR
        ${TransferEntity.STATUS} = 'not_completed' OR
        ${TransferEntity.STATUS} = 'rejected' OR
        ${TransferEntity.STATUS} = 'pending_confirmation' OR
        ${TransferEntity.STATUS} = 'outdated'
        ORDER BY ${TransferEntity.ID} DESC
    """)
    fun getTransfersArchive(): List<TransferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transfers: List<TransferCached>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transfer: TransferCached)

    @Query("""DELETE FROM ${TransferEntity.ENTITY_NAME}""")
    fun deleteAll()
}
