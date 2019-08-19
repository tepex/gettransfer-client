package com.kg.gettransfer.service

import android.app.Service
import android.content.Intent

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.CoordinateInteractor
import com.kg.gettransfer.domain.interactor.GeoInteractor
import com.kg.gettransfer.domain.model.Coordinate

import kotlinx.coroutines.*
import leakcanary.AppWatcher

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class CoordinateService : Service(), KoinComponent {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    private val coordinateInteractor: CoordinateInteractor by inject()
    private val geoInteractor: GeoInteractor by inject()
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
        AppWatcher.objectWatcher.watch(this)
    }

    private fun coordinateProcess() {
        if (serviceAlive) {
            utils.launchSuspend {
                utils.asyncAwait { geoInteractor.getCurrentLocation() }
                    .isSuccess()
                    ?.let { coordinateInteractor.sendOwnCoordinates(Coordinate(null, it.latitude, it.longitude)) }
                delay(DELAY)
                coordinateProcess()
            }
        }
    }

    companion object {
        const val DELAY = 15_000L  // 15 sec
    }
}
