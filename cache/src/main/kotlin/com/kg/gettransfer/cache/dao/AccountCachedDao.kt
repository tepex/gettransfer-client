package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.*
import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.cache.model.TABLE_ACCOUNT

@Dao
interface AccountCachedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: AccountCached)

    @Query("SELECT * FROM $TABLE_ACCOUNT WHERE carrierId = :id")
    fun getAccount(id: Long): AccountCached

    @Update
    fun updateAccount(account: AccountCached)
}