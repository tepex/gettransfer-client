package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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
