package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Transaction

import com.kg.gettransfer.cache.model.MobileConfigsCached
import com.kg.gettransfer.data.model.MobileConfigEntity

@Dao
interface MobileConfigsCachedDao {
    @Query("SELECT * FROM ${MobileConfigEntity.ENTITY_NAME}")
    fun selectAll(): List<MobileConfigsCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(configs: MobileConfigsCached)

    @Query("DELETE FROM ${MobileConfigEntity.ENTITY_NAME}")
    fun deleteAll()

    @Transaction
    fun update(configs: MobileConfigsCached) {
        deleteAll()
        insert(configs)
    }
}