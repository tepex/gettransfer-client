package com.kg.gettransfer.presentation.model

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

data class PolylineModel(val startPoint: LatLng?,
                         val finishPoint: LatLng?,
                         val line: PolylineOptions?,
                         val track: CameraUpdate?)
