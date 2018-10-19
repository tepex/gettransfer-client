package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PromoEntity
import com.kg.gettransfer.remote.model.PromoModel

class PromoMapper(): EntityMapper<PromoModel?, PromoEntity> {
    override fun fromRemote(type: PromoModel?): PromoEntity = PromoEntity(type!!.data)

    override fun toRemote(type: PromoEntity): PromoModel? = null
}