package com.kg.gettransfer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutocompletePredictionsEntity(
    @SerialName(RESULT)      val result: String?,
    @SerialName(PREDICTIONS) val predictions: List<PredictionEntity>?
) {
    companion object {
        const val RESULT = "result"
        const val PREDICTIONS = "predictions"
    }
}

@Serializable
data class PredictionEntity(
    @SerialName(DESCRIPTION) val description: String,
    @SerialName(PLACE_ID)    val placeId: String?,
    @SerialName(TYPES)       val types: List<String>?
) {
    companion object {
        const val DESCRIPTION = "description"
        const val PLACE_ID    = "place_id"
        const val TYPES       = "types"
    }
}