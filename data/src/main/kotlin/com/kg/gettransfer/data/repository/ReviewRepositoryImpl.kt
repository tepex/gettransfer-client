package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.ReviewDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.domain.repository.ReviewRepository
import org.koin.standalone.get
import java.util.*

class ReviewRepositoryImpl(private val remote: ReviewDataStoreRemote): ReviewRepository, BaseRepository() {

    override suspend fun rateTrip(offerId: Long, map: HashMap<String, Int>, comment: String): Result<Any> {
        val result = retrieveEntity { remote.sendReview(offerId, map, comment) }
        return Result(result, (result.error?.let { ExceptionMapper.map(it) }) )
    }
}