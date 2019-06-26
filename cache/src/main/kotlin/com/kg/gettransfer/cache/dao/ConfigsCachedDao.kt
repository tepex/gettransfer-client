package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.kg.gettransfer.cache.model.ConfigsCached
import com.kg.gettransfer.data.model.ConfigsEntity

@Dao
interface ConfigsCachedDao {
    @Query("""SELECT * FROM ${ConfigsEntity.ENTITY_NAME}""")
    fun selectAll(): List<ConfigsCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(configs: ConfigsCached)

    @Query("""DELETE FROM ${ConfigsEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Transaction
    fun update(configs: ConfigsCached) {
        deleteAll()
        insert(configs)
    }
}
