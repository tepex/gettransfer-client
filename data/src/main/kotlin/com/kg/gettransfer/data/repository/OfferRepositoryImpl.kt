package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.PreferencesCache

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository
import java.text.DateFormat
import org.koin.standalone.get
import org.koin.standalone.inject

class OfferRepositoryImpl(
    private val factory: DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>
) : BaseRepository(), OfferRepository {

    private val dateFormat = get<ThreadLocal<DateFormat>>("iso_date")
    private val preferencesCache = get<PreferencesCache>()
    private val offerReceiver: OfferInteractor by inject()

    override var offerViewExpanded: Boolean
        get() = preferencesCache.offerViewExpanded
        set(value) {
            preferencesCache.offerViewExpanded = value
        }

    override fun newOffer(offer: Offer): Result<Offer> {
        log.debug("OfferRepository.newOffer: $offer")
        factory.retrieveCacheDataStore().setOffer(offer.map(dateFormat.get()))
        return Result(offer)
    }

    /*override suspend fun getOffers(id: Long): Result<List<Offer>> =
        retrieveRemoteListModel<OfferEntity, Offer>(mapper) {
            factory.retrieveRemoteDataStore().getOffers(id)
        }*/

    override suspend fun getOffers(id: Long): Result<List<Offer>> {
        val result: ResultEntity<List<OfferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getOffers(id)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().setOffers(result.entity) }
        return Result(
            result.entity?.map { it.map(dateFormat.get()) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getOffersCached(id: Long): Result<List<Offer>> {
        val result: ResultEntity<List<OfferEntity>?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getOffers(id)
        }
        return Result(
            result.entity?.map { it.map(dateFormat.get()) } ?: emptyList(),
            null,
            result.error != null && result.entity != null,
            result.cacheError?.map()
        )
    }

    override fun clearOffersCache() {
        factory.retrieveCacheDataStore().clearOffersCache()
    }

    internal fun onNewOfferEvent(offer: OfferEntity) = offerReceiver.onNewOfferEvent(offer.map(dateFormat.get()))
}
