package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.data.model.OfferEntity

@Dao
interface OfferCachedDao {
    @Query("SELECT * FROM ${OfferEntity.ENTITY_NAME} WHERE ${OfferEntity.ID} = :id")
    fun getOffer(id: Long): OfferCached

    @Query("SELECT * FROM ${OfferEntity.ENTITY_NAME} WHERE ${OfferEntity.TRANSFER_ID} = :transferId")
    fun getOffers(transferId: Long): List<OfferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOffer(offer: OfferCached)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOffers(offers: List<OfferCached>)

    @Query("DELETE FROM ${OfferEntity.ENTITY_NAME}")
    fun deleteAll()
}
