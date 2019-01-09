package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.ReviewRate

data class ReviewRateModel(
    val rateType: ReviewRate.RateType,
    val rateValue: Int
)
