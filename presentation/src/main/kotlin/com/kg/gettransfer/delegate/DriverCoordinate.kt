package com.kg.gettransfer.delegate

import android.os.Handler
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Coordinate
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import kotlin.properties.Delegates

class DriverCoordinate(private val handler: Handler, private val drawCar: () -> Unit): KoinComponent {
    private val interactor: TransferInteractor by inject()

    init { requestCoordinates() }

    var prop: Coordinate by Delegates.observable(Coordinate(0, 0.0f, 0.0f)){
        p, o, n ->
        compute(o, n)
        drawCar.invoke()
        handler.postDelayed({ requestCoordinates() }, 15000)
    }

    private fun requestCoordinates() = interactor.initCoordinatesReceiving()

    private fun compute(old: Coordinate, new: Coordinate){}
}