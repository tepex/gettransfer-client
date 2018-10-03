package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RouteInfoModel(@SerializedName("success") @Expose var success: Boolean,
                     @SerializedName("distance") @Expose var distance: Int?,
                     @SerializedName("duration") @Expose var duration: Int?,
                     @SerializedName("prices") @Expose var prices: Map<String, TransportTypePriceModel>?,
                     @SerializedName("watertaxi") @Expose var watertaxi: Boolean,
                     @SerializedName("routes") @Expose var routes: List<RoutesModel>)

class TransportTypePriceModel(@SerializedName("min_float") @Expose var minFloat: Float,
                              @SerializedName("min") @Expose var min: String,
                              @SerializedName("max") @Expose var max: String)

class RoutesModel(@SerializedName("overview_polyline") @Expose var overviewPolyline: PointsModel,
                  @SerializedName("legs") @Expose var legs: List<LegModel>)

class PointsModel(@SerializedName("points") @Expose var points: String)
class LegModel(@SerializedName("steps") @Expose var steps: List<StepModel>)
class StepModel(@SerializedName("polyline") @Expose var polyline: PolylineModel)
class PolylineModel(@SerializedName("points") @Expose var points: String)
