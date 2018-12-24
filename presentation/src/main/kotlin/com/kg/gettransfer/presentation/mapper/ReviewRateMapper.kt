package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.presentation.model.ReviewRateModel
import java.lang.UnsupportedOperationException

class ReviewRateMapper: Mapper<ReviewRateModel, ReviewRate> {

    override fun fromView(type: ReviewRateModel) =
            ReviewRate(rateType = type.rateType,
                       rateValue = type.rateValue)

    override fun toView(type: ReviewRate) = throw UnsupportedOperationException()

}