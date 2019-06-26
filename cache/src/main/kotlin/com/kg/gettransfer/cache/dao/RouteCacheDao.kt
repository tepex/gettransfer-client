package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.kg.gettransfer.cache.model.RouteInfoCached
import com.kg.gettransfer.data.model.RouteInfoEntity

@Dao
interface RouteCacheDao {

    @Query("""
        SELECT * FROM ${RouteInfoEntity.ENTITY_NAME} WHERE
        ${RouteInfoEntity.FROM_POINT} = :from AND
        ${RouteInfoEntity.TO_POINT} = :to
    """)
    fun getRouteInfo(from: String, to: String): RouteInfoCached?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(routeInfo: RouteInfoCached)
}
