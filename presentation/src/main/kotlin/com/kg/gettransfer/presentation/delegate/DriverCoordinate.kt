package com.kg.gettransfer.presentation.delegate

import android.location.Location
import android.os.Handler
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Coordinate
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.ArrayList
import kotlin.properties.Delegates

class DriverCoordinate(private val handler: Handler,
                       private val coordinateRequester: CoordinateRequester,    // need interface to use in different classes
                       private val callDrawing: (bearing: Float, coordinate: LatLng, show: Boolean) -> Unit) : KoinComponent {
    val list = ArrayList<Pair<Double, Double>>()
//    private var count = 0
    var showMoving: Boolean = false

    init {
        requestCoordinates()
//        list.add(Pair(55.741038, 37.610102))      // data for debug
//        list.add(Pair(55.740501, 37.611184))
//        list.add(Pair(55.741375, 37.612136))
//        list.add(Pair(55.741381, 37.613194))
//        list.add(Pair(55.741746, 37.614165))
//        list.add(Pair(55.741057, 37.614943))
    }

    var property: Coordinate by Delegates.observable(Coordinate(0, ZERO_COORDINATE, ZERO_COORDINATE)) { p, old, new ->
        with(old) {
            if (lat != ZERO_COORDINATE && lon != ZERO_COORDINATE) showMoving = true
        }
        with(handler) {
            val startTime = SystemClock.uptimeMillis()
            post {
                object : Runnable {
                    override fun run() { compute(old, new, startTime, this) }
                }.run()
            }
            postDelayed({ requestCoordinates() }, REQUEST_PERIOD)
        }

    }

    private fun requestCoordinates() = coordinateRequester.request()

    private fun compute(oldLocation: Coordinate, newLocation: Coordinate, time: Long, runnable: Runnable) {
        val bearing = get(oldLocation).bearingTo(get(newLocation))
        val elapsed = SystemClock.uptimeMillis() - time
        val t = LinearInterpolator().getInterpolation(elapsed.toFloat() / INTERPOLATION_CONST)

        val lon = t * newLocation.lon + (1 - t) * oldLocation.lon
        val lat = t * newLocation.lat + (1 - t) * oldLocation.lat

        callDrawing(bearing, LatLng(lat, lon), showMoving)
        if (t < 1.0) handler.postDelayed(runnable, FRAME_DELAY)
    }

    private fun get(coord: Coordinate) = Location(PROVIDER).apply {
        latitude = coord.lat
        longitude = coord.lon
    }

    companion object {
        const val REQUEST_PERIOD = 5_000L       // delay of socket request for driver's coordinates
        const val FRAME_DELAY = 16L             // time of update marker's position due interpolator
        const val INTERPOLATION_CONST = 2000    // the more value the longer marker's moving time, the less the shorter
        const val PROVIDER = "service Provider"
        const val ZERO_COORDINATE = 0.0
    }

    /*
    55.741038, 37.610102
    55.740501, 37.611184
    55.741375, 37.612136
    55.741381, 37.613194
    55.741746, 37.614165
    55.741057, 37.614943
     */
}