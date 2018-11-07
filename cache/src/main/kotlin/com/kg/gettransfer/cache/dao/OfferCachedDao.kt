package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

//import com.kg.gettransfer.cache.model.OfferCached
//import com.kg.gettransfer.cache.model.TABLE_OFFER

@Dao
interface OfferCachedDao {
    /*
    @Query("SELECT * FROM $TABLE_OFFER WHERE id = :id")
    fun getOffer(id: Long): OfferCached

    @Query("SELECT * FROM $TABLE_OFFER WHERE transferId = :transferId")
    fun getOffers(transferId: Long): List<OfferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOffer(offer: OfferCached)
    */
}
