package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.presentation.model.ReviewRateModel
import java.lang.UnsupportedOperationException

open class ReviewRateMapper : Mapper<ReviewRateModel, ReviewRate> {
    override fun fromView(type: ReviewRateModel): ReviewRate = throw UnsupportedOperationException()

    override fun toView(type: ReviewRate) =
        ReviewRateModel(
            rateType = type.rateType,
            rateValue = type.rateValue
        )
}
