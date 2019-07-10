package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.ReviewDataStoreRemote
import com.kg.gettransfer.data.model.map
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.repository.ReviewRepository

class ReviewRepositoryImpl(
    private val remote: ReviewDataStoreRemote
) : ReviewRepository, BaseRepository() {

    override var offerRateID: Long = DEFAULT_ID
    override var comment: String = NO_COMMENT
    override var rates: MutableSet<ReviewRate> = mutableSetOf()

    override suspend fun rateTrip(): Result<Unit> {
        return try {
            rates.forEach { rateItem ->
                retrieveRemoteEntity { remote.sendReview(offerRateID, rateItem.map()) }
            }
            Result(Unit)
        } catch (e: RemoteException) {
            Result(Unit, e.map())
        }
    }

    override suspend fun pushComment(): Result<Unit> {
        return try {
            remote.sendFeedBackComment(offerRateID, comment)
            Result(Unit)
        } catch (e: RemoteException) {
            Result(Unit, e.map())
        }
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
