package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.OfferDataStore
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.OfferDataStoreCache
import com.kg.gettransfer.data.ds.OfferDataStoreRemote

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.OfferMapper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.model.Carrier
import com.kg.gettransfer.domain.model.Money
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Price
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Ratings
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Vehicle
import com.kg.gettransfer.domain.model.VehicleBase

import com.kg.gettransfer.domain.repository.OfferRepository

import java.util.Date
import java.util.Locale

import org.koin.standalone.get

class OfferRepositoryImpl(private val factory: DataStoreFactory<OfferDataStore, OfferDataStoreCache, OfferDataStoreRemote>) :
    BaseRepository(), OfferRepository {

    private val mapper = get<OfferMapper>()

    override fun newOffer(offer: Offer): Result<Offer> {
        log.debug("OfferRepository.newOffer: $offer")
        return Result(offer)
    }

    override suspend fun getOffers(id: Long): Result<List<Offer>> =
        retrieveRemoteListModel<OfferEntity, Offer>(mapper) {
            factory.retrieveRemoteDataStore().getOffers(id)
        }
            /*
            , {
                Offer(0,
              "",
              false,
              false,
              Date(),
              null,
              Price(Money("", null), "", "", 0.0),
              null,
              null,
              Carrier(0, Profile(null, null, null), false, 0, emptyList<Locale>(), Ratings(null, null, null, null), false),
              Vehicle(VehicleBase("", ""), 0, "", TransportType("", 0, 0), emptyList<String>()),
              null)
        })*/
}
