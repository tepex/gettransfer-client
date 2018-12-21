package com.kg.gettransfer.remote

import com.kg.gettransfer.data.ReviewRemote
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.remote.mapper.RateMapper
import com.kg.gettransfer.remote.model.FeedBackToRemote
import com.kg.gettransfer.remote.model.RateModel
import com.kg.gettransfer.remote.model.RateToRemote
import com.kg.gettransfer.remote.model.ResponseModel
import org.koin.core.parameter.parametersOf
import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class ReviewRemoteImpl: ReviewRemote {

    private val core = get<ApiCore>()

    override suspend fun sendReview(id: Long, map: HashMap<String, Int>, comment: String): Any {
        for (entry in map.entries){
            val resp = core.tryTwice { core.api.rateOffer(id, entry.key, RateToRemote(entry.value)) }
        }
        val r = core.tryTwice { core.api.sendFeedBack(id, FeedBackToRemote(comment)) }
        return Any()
    }

}