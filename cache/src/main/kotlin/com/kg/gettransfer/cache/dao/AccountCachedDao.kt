package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

import com.kg.gettransfer.cache.model.AccountCached

@Dao
interface AccountCachedDao {
    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: AccountCached)
    */

    @Query("SELECT * FROM account")
    fun getAccount(): AccountCached

    /*
    @Update
    fun updateAccount(account: AccountCached)
    */
}