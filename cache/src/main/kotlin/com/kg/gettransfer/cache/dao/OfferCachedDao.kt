package com.kg.gettransfer.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kg.gettransfer.cache.model.OfferCached
import com.kg.gettransfer.data.model.OfferEntity

@Dao
interface OfferCachedDao {

    @Query("""SELECT * FROM ${OfferEntity.ENTITY_NAME} WHERE ${OfferEntity.ID} = :id""")
    fun getOffer(id: Long): OfferCached

    @Query("""SELECT * FROM ${OfferEntity.ENTITY_NAME} WHERE ${OfferEntity.TRANSFER_ID} = :transferId""")
    fun getOffers(transferId: Long): List<OfferCached>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOffer(offer: OfferCached)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllOffers(offers: List<OfferCached>)

    @Query("""DELETE FROM ${OfferEntity.ENTITY_NAME}""")
    fun deleteAll()

    @Query("""DELETE FROM ${OfferEntity.ENTITY_NAME} WHERE ${OfferEntity.TRANSFER_ID} = :transferId""")
    fun deleteForTransfer(transferId: Long)

    @Transaction
    fun updateOffersForTransfer(offers: List<OfferCached>) {
        offers.firstOrNull()?.transferId?.let { deleteForTransfer(it) }
        insertAllOffers(offers)
    }
}
