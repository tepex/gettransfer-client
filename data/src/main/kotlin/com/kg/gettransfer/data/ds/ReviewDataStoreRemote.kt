package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.ReviewDataStore
import com.kg.gettransfer.data.ReviewRemote
import org.koin.standalone.inject

class ReviewDataStoreRemote: ReviewDataStore {
    val remote: ReviewRemote by inject()

    override suspend fun sendReview(offerId: Long, mapOfRate: HashMap<String, Int>, comment: String): Any =
            remote.sendReview(offerId, mapOfRate, comment)
}