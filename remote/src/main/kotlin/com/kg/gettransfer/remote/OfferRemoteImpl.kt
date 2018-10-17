package com.kg.gettransfer.remote

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.remote.mapper.OfferMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.OfferModel
import com.kg.gettransfer.remote.model.OffersModel

class OfferRemoteImpl(private val core: ApiCore,
                      private val mapper: OfferMapper): OfferRemote {
    override suspend fun getOffers(id: Long): List<OfferEntity> {
        val response: ResponseModel<OffersModel> = core.tryTwice(id, { _id -> core.api.getOffers(_id) })
        val offers: List<OfferModel> = response.data!!.offers
        return offers.map { mapper.fromRemote(it) }
    }
}
