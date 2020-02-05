package com.kg.gettransfer.extensions

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.core.domain.Point

fun Point.map() = LatLng(latitude, longitude)

fun LatLng.map() = Point(latitude, longitude)
