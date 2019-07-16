package com.kg.gettransfer.remote

import com.kg.gettransfer.data.ReviewRemote
import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.remote.model.FeedBackToRemote
import com.kg.gettransfer.remote.model.map
import org.koin.core.get

class ReviewRemoteImpl : ReviewRemote {

    private val core = get<ApiCore>()

    override suspend fun sendReview(id: Long, reviewRate: ReviewRateEntity) {
        core.tryTwice { core.api.rateOffer(id, reviewRate.rateType, reviewRate.map()) }
    }

    override suspend fun sendFeedBackComment(id: Long, comment: String) {
        core.tryTwice { core.api.sendFeedBack(id, FeedBackToRemote(comment)) }
    }
}
