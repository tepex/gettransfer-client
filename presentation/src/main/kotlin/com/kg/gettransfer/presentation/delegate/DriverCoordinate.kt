package com.kg.gettransfer.presentation.delegate

import android.location.Location
import android.os.Handler
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.model.Coordinate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import kotlin.properties.Delegates

class DriverCoordinate(
        private val handler: Handler,
        private val callDrawing: ((bearing: Float, coordinate: LatLng, show: Boolean) -> Unit)? = null
) : KoinComponent {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)

    private val coordinateInteractor: CoordinateInteractor by inject()

    var showMoving: Boolean = false
    var requestCoordinates = true

    init { requestCoordinates() }

    var property: Coordinate by Delegates.observable(Coordinate(0, ZERO_COORDINATE, ZERO_COORDINATE)) { _, old, new ->
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

    private fun requestCoordinates() {
        if (requestCoordinates) {
            utils.launchSuspend {
                coordinateInteractor.initCoordinatesReceiving()
                delay(REQUEST_PERIOD)
                requestCoordinates()
            }
        }
    }

    private fun compute(oldLocation: Coordinate, newLocation: Coordinate, time: Long, runnable: Runnable) {
        val bearing = get(oldLocation).bearingTo(get(newLocation))
        val elapsed = SystemClock.uptimeMillis() - time
        val t = LinearInterpolator().getInterpolation(elapsed.toFloat() / INTERPOLATION_CONST)

        val lon = t * newLocation.lon + (1 - t) * oldLocation.lon
        val lat = t * newLocation.lat + (1 - t) * oldLocation.lat

        callDrawing?.let { it(bearing, LatLng(lat, lon), showMoving) }
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
}