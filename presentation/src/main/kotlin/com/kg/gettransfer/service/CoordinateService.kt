package com.kg.gettransfer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor
import com.kg.gettransfer.domain.model.Coordinate
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class CoordinateService: Service(), KoinComponent {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    private val coordinateInteractor: CoordinateInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()
    private var serviceAlive = false

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        serviceAlive = true
        coordinateProcess()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceAlive = false
    }

    private fun coordinateProcess(){
        if (serviceAlive) {
            utils.launchSuspend {
                utils.asyncAwait { routeInteractor.getCurrentAddress() }
                        .isNotError()
                        ?.let { it.cityPoint.point }
                        ?.let { coordinateInteractor.sendOwnCoordinates(Coordinate(lat = it.latitude, lon = it.longitude)) }
                delay(DELAY)
                coordinateProcess()
            }
        }
    }

    companion object {
        const val DELAY = 15_000L  // 15 sec
    }
}