package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RouteInfoModel(@SerializedName("success") @Expose val success: Boolean,
                     @SerializedName("distance") @Expose val distance: Int?,
                     @SerializedName("duration") @Expose val duration: Int?,
                     @SerializedName("prices") @Expose val prices: Map<String, TransportTypePriceModel>?,
                     @SerializedName("watertaxi") @Expose val watertaxi: Boolean,
                     @SerializedName("routes") @Expose val routes: List<RoutesModel>?)

class TransportTypePriceModel(@SerializedName("min_float") @Expose val minFloat: Float,
                              @SerializedName("min") @Expose val min: String,
                              @SerializedName("max") @Expose val max: String)

class RoutesModel(@SerializedName("overview_polyline") @Expose val overviewPolyline: PointsModel,
                  @SerializedName("legs") @Expose val legs: List<LegModel>)

class PointsModel(@SerializedName("points") @Expose val points: String)
class LegModel(@SerializedName("steps") @Expose val steps: List<StepModel>)
class StepModel(@SerializedName("polyline") @Expose val polyline: PolylineModel)
class PolylineModel(@SerializedName("points") @Expose val points: String)
