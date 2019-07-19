package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ReviewDataStore
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.ReviewDataStoreCache
import com.kg.gettransfer.data.ds.ReviewDataStoreRemote

import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.OfferRateEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.repository.ReviewRepository

class ReviewRepositoryImpl(
    private val factory: DataStoreFactory<ReviewDataStore, ReviewDataStoreCache, ReviewDataStoreRemote>
) : ReviewRepository, BaseRepository() {

    override var offerRateID: Long = DEFAULT_ID
    override var comment: String = NO_COMMENT
    override var rates: MutableSet<ReviewRate> = mutableSetOf()

    override suspend fun rateTrip(): Result<Unit> {
        rates.forEach { rateItem ->
            val rate = rateItem.map()
            val result: ResultEntity<Unit?> = retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().sendReview(offerRateID, rate)
            }
            if (result.error != null && offerRateID != DEFAULT_ID)
                factory.retrieveCacheDataStore().saveRate(OfferRateEntity(0, offerRateID, rate))
        }
        return Result(Unit)
    }

    override suspend fun pushComment(): Result<Unit> {
        val result: ResultEntity<Unit?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().sendFeedBackComment(offerRateID, comment)
        }
        if (result.error != null) factory.retrieveCacheDataStore().saveFeedback(OfferFeedbackEntity(offerRateID, comment))
        return Result(Unit)
    }

    override suspend fun checkCachedData() {
        checkNotSendedReviews()
        checkNotSendedComments()
    }

    private suspend fun checkNotSendedReviews() {
        factory.retrieveCacheDataStore().getAllRates().forEach { offerReview ->
            val result: ResultEntity<Unit?> = retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().sendReview(offerReview.offerId, offerReview.reviewRate)
            }
            if (result.error == null) factory.retrieveCacheDataStore().deleteRate(offerReview.id)
        }
    }

    private suspend fun checkNotSendedComments() {
        factory.retrieveCacheDataStore().getAllFeedbacks().forEach { comment ->
            val result: ResultEntity<Unit?> = retrieveRemoteEntity {
                factory.retrieveRemoteDataStore().sendFeedBackComment(comment.offerId, comment.comment)
            }
            if (result.error == null) factory.retrieveCacheDataStore().deleteOfferFeedback(comment.offerId)
        }
    }

    override suspend fun clearReviewCache() {
        factory.retrieveCacheDataStore().deleteReviews()
    }

    // call this when leave root screen (not dialogs!) with possible review working
    override fun releaseReviewData() {
        rates.clear()
        comment = NO_COMMENT
        offerRateID = DEFAULT_ID
    }

    companion object {
        const val DEFAULT_ID = 0L
        const val NO_COMMENT = ""
    }
}
