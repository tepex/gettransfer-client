package com.kg.gettransfer.domain.model

data class RouteInfo(var success: Boolean,
                     var distance: Int?,
                     var duration: Int?,
                     var prices: List<TransportTypePrice>?,
                     var watertaxi: Boolean,
                     var polyLines: List<String>,
                     var overviewPolyline: String)

data class TransportTypePrice(var tranferId: String,
                              var minFloat: Float,
                              var min: String,
                              var max: String)
