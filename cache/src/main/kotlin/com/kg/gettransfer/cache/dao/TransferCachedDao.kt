package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kg.gettransfer.cache.model.TABLE_TRANSFER
import com.kg.gettransfer.cache.model.TransferCached

@Dao
interface TransferCachedDao {
    @Query("SELECT * FROM $TABLE_TRANSFER WHERE id = :id")
    fun getTransfer(id: Long): TransferCached

    @Query("SELECT * FROM $TABLE_TRANSFER ORDER BY id DESC")
    fun getAllTransfers(): List<TransferCached>

    @Query("SELECT * FROM $TABLE_TRANSFER WHERE NOT status = 'completed' ORDER BY id DESC")
    fun getTransfersCompleted(): List<TransferCached>

    @Query("SELECT * FROM $TABLE_TRANSFER WHERE status = 'new' OR  status = 'draft' OR status = 'performed' OR status ='pending_confirmation' ORDER BY id DESC")
    fun getTransfersActive(): List<TransferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(transfers: List<TransferCached>)
}
