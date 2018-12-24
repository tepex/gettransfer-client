package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.ReviewRateEntity
import com.kg.gettransfer.domain.model.ReviewRate

open class ReviewRateMapper : Mapper<ReviewRateEntity, ReviewRate> {
    override fun fromEntity(type: ReviewRateEntity): ReviewRate = throw UnsupportedOperationException()

    override fun toEntity(type: ReviewRate) =
        ReviewRateEntity(
            rateType = type.rateType.type,
            value = type.rateValue
        )
}
