package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.RateEntity
import com.kg.gettransfer.remote.model.RateModel
import com.kg.gettransfer.remote.model.RateToRemote

open class RateMapper {
     fun fromRemote(type: RateModel) =
            RateEntity(type.offer_id,
                       type.rating_type,
                       type.value)

     fun toRemote(rate: Int) = RateToRemote(rate)

}