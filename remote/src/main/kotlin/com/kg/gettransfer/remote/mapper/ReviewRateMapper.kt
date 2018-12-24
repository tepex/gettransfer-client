package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.mapper.Mapper
import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.remote.model.RateToRemote
import java.lang.UnsupportedOperationException

open class ReviewRateMapper : Mapper<ReviewRateEntity, RateToRemote> {

     override fun fromEntity(type: ReviewRateEntity) = RateToRemote(rating = type.value)
     override fun toEntity(type: RateToRemote): ReviewRateEntity = throw UnsupportedOperationException()
}
