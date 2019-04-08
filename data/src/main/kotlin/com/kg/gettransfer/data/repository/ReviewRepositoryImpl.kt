package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.ds.ReviewDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.ReviewRateMapper

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.repository.ReviewRepository

import org.koin.standalone.get

class ReviewRepositoryImpl(private val remote: ReviewDataStoreRemote) : ReviewRepository, BaseRepository() {
    private val rateMapper = get<ReviewRateMapper>()

    override suspend fun rateTrip(offerId: Long, list: List<ReviewRate>, comment: String): Result<Unit> {
        return try {
            list.forEach { rate ->
                retrieveRemoteEntity { remote.sendReview(offerId, rateMapper.toEntity(rate)) }
            }
            remote.sendFeedBackComment(offerId, comment)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

    override suspend fun sendComment(offerId: Long, comment: String): Result<Unit> {
        return try {
            remote.sendFeedBackComment(offerId, comment)
            Result(Unit)
        } catch (e: RemoteException) { Result(Unit, ExceptionMapper.map(e)) }
    }

}
