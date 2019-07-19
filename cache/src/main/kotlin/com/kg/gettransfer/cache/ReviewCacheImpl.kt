package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.ReviewCache
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity

import org.koin.core.KoinComponent
import org.koin.core.inject

class ReviewCacheImpl : ReviewCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override suspend fun insertRate(offerRate: OfferRateEntity) {
        db.reviewCacheDao().deleteRate(offerRate.offerId, offerRate.reviewRate.rateType)
        db.reviewCacheDao().insertRate(offerRate.map())
    }

    override suspend fun getAllRates() = db.reviewCacheDao().getAllRates().map { it.map() }

    override suspend fun deleteRate(rateId: Long) = db.reviewCacheDao().deleteRate(rateId)


    override suspend fun insertFeedback(offerFeedback: OfferFeedbackEntity) = db.reviewCacheDao().insertFeedback(offerFeedback.map())

    override suspend fun getAllFeedbacks() = db.reviewCacheDao().getAllFeedbacks().map { it.map() }

    override suspend fun deleteOfferFeedback(offerId: Long) = db.reviewCacheDao().deleteOfferFeedback(offerId)


    override suspend fun deleteReviews() {
        db.reviewCacheDao().deleteAllRates()
        db.reviewCacheDao().deleteAllFeedbacks()
    }
}
