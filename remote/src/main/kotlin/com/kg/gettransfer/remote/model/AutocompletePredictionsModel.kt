package com.kg.gettransfer.remote.model

import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.AutocompletePredictionsEntity
import com.kg.gettransfer.data.model.PredictionEntity

data class AutocompletePredictionsModel(
        @SerializedName(AutocompletePredictionsEntity.RESULT)      val result: String?,
        @SerializedName(AutocompletePredictionsEntity.PREDICTIONS) val predictions: List<PredictionModel>?
)

data class PredictionModel(
        @SerializedName(PredictionEntity.DESCRIPTION) val description: String,
        @SerializedName(PredictionEntity.PLACE_ID)    val placeId: String?,
        @SerializedName(PredictionEntity.TYPES)       val types: List<String>?
)