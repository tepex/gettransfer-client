package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote

import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.OfferRepository

import org.koin.standalone.get

class OfferRepositoryImpl(private val factory: DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>) :
    BaseRepository(), OfferRepository {

    private val mapper = get<OfferMapper>()

    override fun newOffer(offer: Offer): Result<Offer> {
        log.debug("OfferRepository.newOffer: $offer")
        factory.retrieveCacheDataStore().setOffer(mapper.toEntity(offer))
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
        return if(result.error != null) Result(factory.retrieveCacheDataStore().getOffers(id).map { mapper.fromEntity(it) })
        else Result(result.entity?.map { mapper.fromEntity(it) }?: emptyList())
    }

    override fun clearOffersCache(){
        factory.retrieveCacheDataStore().clearOffersCache()
    }

    /*companion object {
        private val DEFAULT =
            Offer(
                    0,
                    0,
                    "",
                    false,
                    false,
                    Date(),
                    null,
                    Price(Money("", null), null, "", "", 0.0),
                    null,
                    null,
                    Carrier(0, Profile(null, null, null), false, 0, emptyList<Locale>(), Ratings(null, null, null, null), false),
                    Vehicle(0, "", "", 0, null, TransportType(TransportType.ID.ECONOMY, 0, 0), emptyList<String>()),
                    null
                )
    }*/
}
