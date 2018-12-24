package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.ReviewRateEntity

class RateModel(@SerializedName (ReviewRateEntity.RATED_OFFER_ID) @Expose val offer_id: Long,
                @SerializedName (ReviewRateEntity.RATE_TYPE)      @Expose val rating_type: String,
                @SerializedName (ReviewRateEntity.TOTAL_RATING)   @Expose val value: Float)