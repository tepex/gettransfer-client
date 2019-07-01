package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.SystemDataStoreRemote

import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.domain.repository.SystemRepository

import org.koin.core.get

class SystemRepositoryImpl(
    private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>
) : BaseRepository(), SystemRepository {

    private val preferencesCache = get<PreferencesCache>()

    override var lastMode: String
        get() = preferencesCache.lastMode
        set(value) { preferencesCache.lastMode = value }

    override var lastMainScreenMode: String
        get() = preferencesCache.lastMainScreenMode
        set(value) { preferencesCache.lastMainScreenMode = value }

    override var lastCarrierTripsTypeView: String
        get() = preferencesCache.lastCarrierTripsTypeView
        set(value) { preferencesCache.lastCarrierTripsTypeView = value }

    override var firstDayOfWeek: Int
        get() = preferencesCache.firstDayOfWeek
        set(value) { preferencesCache.firstDayOfWeek = value }

    override var isFirstLaunch: Boolean
        get() = preferencesCache.isFirstLaunch
        set(value) { preferencesCache.isFirstLaunch = value }

    override var isOnboardingShowed: Boolean
        get() = preferencesCache.isOnboardingShowed
        set(value) { preferencesCache.isOnboardingShowed = value }

    override var selectedField: String
        get() = preferencesCache.selectedField
        set(value) { preferencesCache.selectedField = value }

    override val endpoints = preferencesCache.endpoints.map { it.map() }

    override var endpoint: Endpoint
        get() = preferencesCache.endpoint.map()
        set(value) {
            val endpointEntity = value.map()
            preferencesCache.endpoint = endpointEntity
        }

    override var addressHistory: List<GTAddress>
        get() = preferencesCache.addressHistory.map { it.map() }
        set(value) { preferencesCache.addressHistory = value.map { it.map() } }

    override var appEnters: Int
        get() = preferencesCache.appEnters
        set(value) { preferencesCache.appEnters = value }

    override var isDebugMenuShowed: Boolean
        get() = preferencesCache.isDebugMenuShowed
        set(value) { preferencesCache.isDebugMenuShowed = value }

    override var mobileConfig = MobileConfig.EMPTY
        private set

    override suspend fun coldStart(): Result<Unit> {
        if (mobileConfig === MobileConfig.EMPTY) {
            val result: ResultEntity<MobileConfigEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getMobileConfigs()
            }
            if (result.error != null && result.entity == null) return Result(Unit, result.error.map())

            result.entity?.let { entity ->
                mobileConfig = entity.map()
                if (result.error == null) factory.retrieveCacheDataStore().setMobileConfigs(entity)
            }
        }
        return Result(Unit)
    }
}
