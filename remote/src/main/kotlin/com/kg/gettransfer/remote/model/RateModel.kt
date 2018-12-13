package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.RateEntity

class RateModel(@SerializedName (RateEntity.RATED_OFFER_ID) @Expose val offer_id: Long,
                @SerializedName (RateEntity.RATE_TYPE)      @Expose val rating_type: String,
                @SerializedName (RateEntity.TOTAL_RATING)   @Expose val value: Float)