package com.kg.gettransfer.sys.cache

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction

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
