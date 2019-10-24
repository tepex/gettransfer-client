package com.kg.gettransfer.sys.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction

import com.kg.gettransfer.sys.data.MobileConfigsEntity

@Dao
interface MobileConfigsDao {
    @Query("""SELECT * FROM ${MobileConfigsEntity.ENTITY_NAME}""")
    fun selectAll(): List<MobileConfigsModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(configs: MobileConfigsModel)

    @Query("""DELETE FROM ${MobileConfigsEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Transaction
    fun update(configs: MobileConfigsModel) {
        deleteAll()
        insert(configs)
    }
}
