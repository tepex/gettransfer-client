package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.ReviewDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.repository.ReviewRepository

import org.koin.standalone.get

class ReviewRepositoryImpl(private val remote: ReviewDataStoreRemote) : ReviewRepository, BaseRepository() {

    override var currentComment: String = NO_COMMENT
    override var currentOfferRateID: Long = DEFAULT_ID
    override var rates: MutableSet<ReviewRate> = mutableSetOf()

    override suspend fun rateTrip(): Result<Unit> {
        return try {
            rates.forEach { rateItem ->
                retrieveRemoteEntity { remote.sendReview(currentOfferRateID, rateItem.map()) }
            }
            Result(Unit)
        } catch (e: RemoteException) {
            Result(Unit, ExceptionMapper.map(e))
        }
    }

    override suspend fun sendComment(offerId: Long, comment: String): Result<Unit> {
        return try {
            remote.sendFeedBackComment(offerId, comment)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

    override suspend fun pushComment(): Result<Unit> {
        return try {
            remote.sendFeedBackComment(currentOfferRateID, currentComment)
            Result(Unit)
        } catch (e: RemoteException) {
            Result(Unit, ExceptionMapper.map(e))
        }
    }

    override suspend fun pushTopRates(): Result<Unit> {
        rates.add(ReviewRate(ReviewRate.RateType.VEHICLE,     MAX_RATE))
        rates.add(ReviewRate(ReviewRate.RateType.DRIVER,      MAX_RATE))
        rates.add(ReviewRate(ReviewRate.RateType.PUNCTUALITY, MAX_RATE))
        return rateTrip()
    }

    //call this when leave root screen (not dialogs!) with possible review working
    override fun releaseReviewData() {
        rates.clear()
        currentComment = NO_COMMENT
        currentOfferRateID = DEFAULT_ID
    }

    companion object {
        const val DEFAULT_ID = 0L
        const val NO_COMMENT = ""
        const val MAX_RATE = 5
    }
}
