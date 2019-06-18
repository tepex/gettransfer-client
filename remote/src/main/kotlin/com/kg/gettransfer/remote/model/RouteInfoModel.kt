package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.RouteInfoEntity

data class RouteInfoModel(
    @SerializedName(RouteInfoEntity.SUCCESS) @Expose val success: Boolean,
    @SerializedName(RouteInfoEntity.DISTANCE) @Expose val distance: Int?,
    @SerializedName(RouteInfoEntity.DURATION) @Expose val duration: Int?,
    @SerializedName(RouteInfoEntity.PRICES) @Expose val prices: Map<String, TransportTypePriceModel>?,
    @SerializedName(RouteInfoEntity.WATERTAXI) @Expose val watertaxi: Boolean,
    @SerializedName(RouteInfoEntity.ROUTES) @Expose val routes: List<RoutesModel>?,
    @SerializedName(RouteInfoEntity.HINTS_TO_COMMENTS) @Expose val hintsToComments: List<String>?
)

data class RoutesModel(
    @SerializedName("overview_polyline") @Expose val overviewPolyline: PointsModel,
    @SerializedName("legs") @Expose val legs: List<LegModel>
)

data class PointsModel(@SerializedName("points") @Expose val points: String)
data class LegModel(@SerializedName("steps") @Expose val steps: List<StepModel>)
data class StepModel(@SerializedName("polyline") @Expose val polyline: PolylineModel)
data class PolylineModel(@SerializedName("points") @Expose val points: String)
