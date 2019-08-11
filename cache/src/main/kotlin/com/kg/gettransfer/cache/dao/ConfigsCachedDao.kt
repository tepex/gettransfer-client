package com.kg.gettransfer.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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
