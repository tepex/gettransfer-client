package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote
import com.kg.gettransfer.data.ds.ReviewDataStoreCache

import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.OfferRateEntity
import com.kg.gettransfer.data.model.OfferFeedbackEntity
import com.kg.gettransfer.data.model.RatingsEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.ReviewRate

import com.kg.gettransfer.domain.repository.OfferRepository

import java.text.DateFormat

import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named

import java.util.Locale

class OfferRepositoryImpl(
    private val factory: DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>,
    private val reviewCache: ReviewDataStoreCache
) : BaseRepository(), OfferRepository {

    private val dateFormat = get<ThreadLocal<DateFormat>>(named("iso_date"))
    private val offerReceiver: OfferInteractor by inject()

    override fun newOffer(offer: Offer): Result<Offer> {
        log.debug("OfferRepository.newOffer: $offer")
        factory.retrieveCacheDataStore().setOffer(offer.map(dateFormat.get()))
        return Result(offer)
    }

    override suspend fun getOffers(id: Long): Result<List<Offer>> {
        val result: ResultEntity<List<OfferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getOffers(id)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().setOffers(result.entity) }
        return Result(
            result.entity?.let {
                checkCachedReview(it)
            }?.map {
                it.map(dateFormat.get())
            } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getOffersCached(id: Long): Result<List<Offer>> {
        val result: ResultEntity<List<OfferEntity>?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getOffers(id)
        }
        return Result(
            result.entity?.let {
                checkCachedReview(it)
            }?.map {
                it.map(dateFormat.get())
            } ?: emptyList(),
            null,
            result.error != null && result.entity != null,
            result.cacheError?.map()
        )
    }

    private suspend fun checkCachedReview(offers: List<OfferEntity>): List<OfferEntity> {
        val cachedRatesResult = retrieveCacheEntity { reviewCache.getAllRates() }.entity
        val cachedFeedbacksResult = retrieveCacheEntity { reviewCache.getAllFeedbacks() }.entity

        return offers.map { item ->
            if (!cachedRatesResult.isNullOrEmpty()) checkOfferCachedRates(item, cachedRatesResult) else item
        }.map { item ->
            if (!cachedFeedbacksResult.isNullOrEmpty()) checkOfferCachedFeedback(item, cachedFeedbacksResult) else item
        }
    }

    private fun checkOfferCachedRates(offer: OfferEntity, rates: List<OfferRateEntity>): OfferEntity {
        val offerRates = rates.filter { it.offerId == offer.id }

        with(offer.ratings) {
            return if (offerRates.isNullOrEmpty()) {
                offer
            } else {
                offer.copy(
                    ratings = RatingsEntity(
                        vehicle = findRating(this?.vehicle, offerRates, ReviewRate.RateType.VEHICLE),
                        driver = findRating(this?.driver, offerRates, ReviewRate.RateType.DRIVER),
                        communication = findRating(this?.communication, offerRates, ReviewRate.RateType.COMMUNICATION)
                    )
                )
            }
        }
    }

    private fun findRating(offerRate: Double?, rates: List<OfferRateEntity>, rateType: ReviewRate.RateType): Double? {
        return rates.find {
            ReviewRate.RateType.valueOf(it.reviewRate.rateType.toUpperCase(Locale.US)) == rateType
        }?.reviewRate?.value?.toDouble() ?: offerRate
    }

    private fun checkOfferCachedFeedback(offer: OfferEntity, feedbacks: List<OfferFeedbackEntity>): OfferEntity {
        val offerComment = feedbacks.find { it.offerId == offer.id }?.comment

        return if (offerComment.isNullOrEmpty()) offer else offer.copy(passengerFeedback = offerComment)
    }

    override fun clearOffersCache() { factory.retrieveCacheDataStore().clearOffersCache() }

    internal fun onNewOfferEvent(offer: OfferEntity) = offerReceiver.onNewOfferEvent(offer.map(dateFormat.get()))
}
