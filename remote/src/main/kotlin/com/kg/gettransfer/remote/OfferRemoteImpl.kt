package com.kg.gettransfer.remote

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.OfferModel
import com.kg.gettransfer.remote.model.OffersModel
import com.kg.gettransfer.remote.model.map
import org.koin.core.get

class OfferRemoteImpl : OfferRemote {

    private val core   = get<ApiCore>()

    override suspend fun getOffers(id: Long): List<OfferEntity> {
        val response: ResponseModel<OffersModel> = core.tryTwice(id) { _id -> core.api.getOffers(_id) }
        @Suppress("UnsafeCallOnNullableType")
        val offers: List<OfferModel> = response.data!!.offers
        offers.forEach { it.vehicle.photos = it.vehicle.photos.map { photo -> core.apiUrl.plus(photo) } }
        return offers.map { it.map(id) }
    }
}
