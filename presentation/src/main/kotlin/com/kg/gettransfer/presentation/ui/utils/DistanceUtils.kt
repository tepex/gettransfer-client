package com.kg.gettransfer.presentation.ui.utils

import android.location.Location

import com.kg.gettransfer.core.domain.Point
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.model.DistanceUnit

import org.koin.core.KoinComponent
import org.koin.core.inject

object DistanceUtils : KoinComponent {

    private val sessionInteractor: SessionInteractor by inject()

    fun getPointToPointDistance(from: Point, to: Point):Int {
        val kmDistance = (point2Location(from).distanceTo(point2Location(to)) / 1000).toInt()
        return if (sessionInteractor.distanceUnit == DistanceUnit.KM) {
            kmDistance
        } else {
            DistanceUnit.kmToMi(kmDistance)
        }
    }

    private fun point2Location(point: Point) = Location("").apply {
        latitude = point.latitude
        longitude = point.longitude
    }
}
