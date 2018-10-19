package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.model.PromoEntity
import com.kg.gettransfer.remote.mapper.PromoMapper
import com.kg.gettransfer.remote.model.PromoModel
import com.kg.gettransfer.remote.model.ResponseModel

class PromoRemoteImpl(val core: ApiCore,
                      val mapper: PromoMapper):  PromoRemote{
    override suspend fun getDiscount(code: String) {

        val response: ResponseModel<PromoModel> = core.api.getDiscount(code).await()

    }


}