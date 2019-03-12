package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.kg.gettransfer.cache.model.TransferCached
import com.kg.gettransfer.data.model.TransferEntity

@Dao
interface TransferCachedDao {
    @Query("SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE ${TransferEntity.ID} = :id")
    fun getTransfer(id: Long): TransferCached?

    @Query("SELECT * FROM ${TransferEntity.ENTITY_NAME} ORDER BY ${TransferEntity.ID} DESC")
    fun getAllTransfers(): List<TransferCached>

    @Query("SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE ${TransferEntity.STATUS} = 'completed' OR ${TransferEntity.STATUS} = 'not_completed' ORDER BY ${TransferEntity.ID} DESC")
    fun getTransfersCompleted(): List<TransferCached>

    @Query("SELECT * FROM ${TransferEntity.ENTITY_NAME} WHERE ${TransferEntity.STATUS} = 'new' OR ${TransferEntity.STATUS} = 'draft' OR ${TransferEntity.STATUS} = 'performed' ORDER BY ${TransferEntity.ID} DESC")
    fun getTransfersActive(): List<TransferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transfers: List<TransferCached>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transfer: TransferCached)

    @Query("DELETE FROM ${TransferEntity.ENTITY_NAME}")
    fun deleteAll()
}
