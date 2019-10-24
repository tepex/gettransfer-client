package com.kg.gettransfer.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

import com.kg.gettransfer.cache.model.AccountCached
import com.kg.gettransfer.data.model.AccountEntity

@Dao
interface AccountCachedDao {

    @Query("""SELECT * FROM ${AccountEntity.ENTITY_NAME}""")
    fun selectAll(): List<AccountCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(account: AccountCached)

    @Query("""DELETE FROM ${AccountEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Transaction
    fun update(account: AccountCached) {
        deleteAll()
        insert(account)
    }
}
