package com.kg.gettransfer.sys.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

import com.kg.gettransfer.sys.data.ConfigsEntity

@Dao
interface ConfigsDao {
    @Query("""SELECT * FROM ${ConfigsEntity.ENTITY_NAME}""")
    fun selectAll(): List<ConfigsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(configs: ConfigsModel)

    @Query("""DELETE FROM ${ConfigsEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Transaction
    fun update(configs: ConfigsModel) {
        deleteAll()
        insert(configs)
    }
}
