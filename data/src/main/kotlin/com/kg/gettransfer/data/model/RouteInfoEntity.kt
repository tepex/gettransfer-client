package com.kg.gettransfer.data.model

data class RouteInfoEntity(val success: Boolean,
                           val distance: Int?,
                           val duration: Int?,
                           val prices: List<TransportTypePriceEntity>?,
                           val watertaxi: Boolean,
                           val polyLines: List<String>?,
                           val overviewPolyline: String?)

data class TransportTypePriceEntity(val transferId: String,
                                    val minFloat: Float,
                                    val min: String,
                                    val max: String)
