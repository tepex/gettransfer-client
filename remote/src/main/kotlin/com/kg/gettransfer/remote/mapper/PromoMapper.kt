package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.PromoEntity
import com.kg.gettransfer.remote.model.PromoModel

class PromoMapper(): EntityMapper<String?, PromoEntity> {
    override fun fromRemote(type: String?): PromoEntity = PromoEntity(type!!)

    override fun toRemote(type: PromoEntity): String? = null
}