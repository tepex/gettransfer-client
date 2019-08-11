package com.kg.gettransfer.sys.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

import com.kg.gettransfer.sys.data.PreferencesEntity

@Dao
interface PreferencesDao {
    @Query("""SELECT * FROM ${PreferencesEntity.ENTITY_NAME}""")
    fun selectAll(): List<PreferencesModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferences: PreferencesModel)

    @Query("""DELETE FROM ${PreferencesEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Transaction
    fun update(preferences: PreferencesModel) {
        deleteAll()
        insert(preferences)
    }
}
