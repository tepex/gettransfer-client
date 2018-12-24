package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ReviewDataStore
import com.kg.gettransfer.data.ReviewRemote
import com.kg.gettransfer.data.model.ReviewRateEntity
import org.koin.standalone.inject

class ReviewDataStoreRemote: ReviewDataStore {
    private val remote: ReviewRemote by inject()

    override suspend fun sendReview(offerId: Long, reviewRate: ReviewRateEntity): Unit =
            remote.sendReview(offerId, reviewRate)

    override suspend fun sendFeedBackComment(offerId: Long, comment: String): Unit =
            remote.sendFeedBackComment(offerId, comment)

}