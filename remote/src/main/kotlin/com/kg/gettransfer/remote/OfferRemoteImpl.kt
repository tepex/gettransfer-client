package com.kg.gettransfer.remote

import com.kg.gettransfer.data.OfferRemote
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.remote.mapper.OfferMapper

import com.kg.gettransfer.remote.model.ResponseModel
import com.kg.gettransfer.remote.model.OfferModel
import com.kg.gettransfer.remote.model.OffersModel

import org.koin.core.parameter.parametersOf

import org.koin.standalone.get
import org.koin.standalone.inject

import org.slf4j.Logger

class OfferRemoteImpl : OfferRemote {
    private val core   = get<ApiCore>()
    private val mapper = get<OfferMapper>()
    private val log: Logger by inject { parametersOf("GTR-remote") }

    override suspend fun getOffers(id: Long): List<OfferEntity> {
        val response: ResponseModel<OffersModel> = core.tryTwice(id, { _id -> core.api.getOffers(_id) })
        /*
        log.debug("offers: ${response.data}")
        return emptyList<OfferEntity>()
        */
        val offers: List<OfferModel> = response.data!!.offers
        //offers.forEach { it.vehicle.photos = it.vehicle.photos.map { photo -> core.apiUrl.plus(photo) } }
        mapper.transferId = id
        return offers.map { mapper.fromRemote(it) }
        */
        return emptyList<OfferEntity>()
        }
        catch(e: Exception) {
            log.error("remote offer error", e)
            return emptyList<OfferEntity>()
        }
    }
}
