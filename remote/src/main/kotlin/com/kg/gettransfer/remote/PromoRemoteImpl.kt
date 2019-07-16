package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PromoRemote
import com.kg.gettransfer.data.model.PromoDiscountEntity
import com.kg.gettransfer.remote.model.ResponseModel
import org.koin.core.get

class PromoRemoteImpl : PromoRemote {

    private val core = get<ApiCore>()

    override suspend fun getDiscount(code: String): PromoDiscountEntity {
        val response: ResponseModel<String> = core.tryTwice { core.api.getDiscount(code) }
        @Suppress("UnsafeCallOnNullableType")
        return PromoDiscountEntity(response.data!!)
    }
}
