package com.kg.gettransfer.cache.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy

import com.kg.gettransfer.cache.model.OfferFeedbackCached
import com.kg.gettransfer.cache.model.OfferRateCached

import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity
import com.kg.gettransfer.data.model.ReviewRateEntity

@Dao
interface ReviewCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRate(offerRate: OfferRateCached)

    @Query("""SELECT * FROM ${OfferRateEntity.ENTITY_NAME}""")
    fun getAllRates(): List<OfferRateCached>

    @Query("""DELETE FROM ${OfferRateEntity.ENTITY_NAME}""")
    fun deleteAllRates()

    @Query("""
        DELETE FROM ${OfferRateEntity.ENTITY_NAME} WHERE
        ${OfferRateEntity.OFFER_ID} = :offerId AND
        ${OfferRateEntity.REVIEW_RATE.plus(ReviewRateEntity.RATE_TYPE)} = :rateType"""
    )
    fun deleteRate(offerId: Long, rateType: String)

    @Query("""DELETE FROM ${OfferRateEntity.ENTITY_NAME} WHERE ${OfferRateEntity.ID} = :rateId""")
    fun deleteRate(rateId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeedback(offerFeedback: OfferFeedbackCached)

    @Query("""SELECT * FROM ${OfferFeedbackEntity.ENTITY_NAME}""")
    fun getAllFeedbacks(): List<OfferFeedbackCached>

    @Query("""DELETE FROM ${OfferFeedbackEntity.ENTITY_NAME}""")
    fun deleteAllFeedbacks()

    @Query("""
        DELETE FROM ${OfferFeedbackEntity.ENTITY_NAME} WHERE
        ${OfferFeedbackEntity.OFFER_ID} = :offerId"""
    )
    fun deleteOfferFeedback(offerId: Long)
}
