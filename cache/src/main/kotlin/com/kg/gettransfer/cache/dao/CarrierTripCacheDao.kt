package com.kg.gettransfer.cache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.kg.gettransfer.cache.model.CarrierTripBaseCached
import com.kg.gettransfer.cache.model.CarrierTripMoreCached
import com.kg.gettransfer.data.model.CarrierTripBaseEntity
import com.kg.gettransfer.data.model.CarrierTripEntity

@Dao
interface CarrierTripCacheDao {

    @Query("""SELECT * FROM ${CarrierTripBaseEntity.ENTITY_NAME} ORDER BY ${CarrierTripBaseEntity.ID}""")
    fun getAllCarrierTripsBase(): List<CarrierTripBaseCached>

    @Query("""SELECT * FROM ${CarrierTripBaseEntity.ENTITY_NAME} WHERE ${CarrierTripBaseEntity.ID} = :id""")
    fun getCarrierTripBase(id: Long): CarrierTripBaseCached

    @Query("""SELECT * FROM ${CarrierTripEntity.ENTITY_NAME_MORE} WHERE ${CarrierTripBaseEntity.ID} = :id""")
    fun getCarrierTripMore(id: Long): CarrierTripMoreCached?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCarrierTripsBase(trips: List<CarrierTripBaseCached>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarrierTripBase(trip: CarrierTripBaseCached)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCarrierTripMore(trip: CarrierTripMoreCached)

    @Query("""DELETE FROM ${CarrierTripBaseEntity.ENTITY_NAME}""")
    fun deleteAllCarrierTripsBase()

    @Query("""DELETE FROM ${CarrierTripEntity.ENTITY_NAME_MORE}""")
    fun deleteAllCarrierTripsMore()
}
