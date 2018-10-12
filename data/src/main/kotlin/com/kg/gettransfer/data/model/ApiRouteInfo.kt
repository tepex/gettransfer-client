package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ApiRouteInfo(@SerializedName("success") @Expose var success: Boolean,
                   @SerializedName("distance") @Expose var distance: Int?,
                   @SerializedName("duration") @Expose var duration: Int?,
                   @SerializedName("prices") @Expose var prices: Map<String, ApiTransportTypePrice>?,
                   @SerializedName("watertaxi") @Expose var watertaxi: Boolean,
                   @SerializedName("routes") @Expose var routes: List<ApiRoutes>?)

class ApiTransportTypePrice(@SerializedName("min_float") @Expose var minFloat: Float,
                            @SerializedName("min") @Expose var min: String,
                            @SerializedName("max") @Expose var max: String)

class ApiRoutes(@SerializedName("overview_polyline") @Expose var overviewPolyline: ApiPoints,
                @SerializedName("legs") @Expose var legs: List<ApiLeg>)

class ApiPoints(@SerializedName("points") @Expose var points: String)

class ApiLeg(@SerializedName("steps") @Expose var steps: List<ApiStep>)

class ApiStep(@SerializedName("polyline") @Expose var polyline: ApiPolyline)

class ApiPolyline(@SerializedName("points") @Expose var points: String)
