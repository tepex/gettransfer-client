package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.AutocompletePredictionsEntity
import com.kg.gettransfer.data.model.PredictionEntity
import com.kg.gettransfer.remote.model.AutocompletePredictionsModel
import com.kg.gettransfer.remote.model.PredictionModel
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

open class AutocompletePredictionsMapper : EntityMapper<AutocompletePredictionsModel, AutocompletePredictionsEntity> {
    private val predictionMapper = get<PredictionMapper>()

    override fun fromRemote(type: AutocompletePredictionsModel) =
        AutocompletePredictionsEntity(
            result = type.result,
            predictions = type.predictions?.map { predictionMapper.fromRemote(it) }
        )

    override fun toRemote(type: AutocompletePredictionsEntity): AutocompletePredictionsModel {
        throw UnsupportedOperationException()
    }
}

open class PredictionMapper : EntityMapper<PredictionModel, PredictionEntity> {
    override fun fromRemote(type: PredictionModel) =
        PredictionEntity(
            description = type.description,
            placeId = type.placeId,
            types = type.types
            )

    override fun toRemote(type: PredictionEntity): PredictionModel {
        throw UnsupportedOperationException()
    }
}