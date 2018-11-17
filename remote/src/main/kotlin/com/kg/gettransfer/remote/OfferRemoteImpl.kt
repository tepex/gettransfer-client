package com.kg.gettransfer.remote

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.remote.mapper.OfferMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.OfferModel
import com.kg.gettransfer.remote.model.OffersModel

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

class OfferRemoteImpl: OfferRemote, KoinComponent {
    private val core: ApiCore by inject()
    private val mapper: OfferMapper by inject()
    
    override suspend fun getOffers(id: Long): List<OfferEntity> {
        val response: ResponseModel<OffersModel> = core.tryTwice(id, { _id -> core.api.getOffers(_id) })
        val offers: List<OfferModel> = response.data!!.offers
        offers.forEach { it.vehicle.photos = it.vehicle.photos.map { photo -> core.apiUrl.plus(photo) } }
        return offers.map { mapper.fromRemote(it) }
    }
}
