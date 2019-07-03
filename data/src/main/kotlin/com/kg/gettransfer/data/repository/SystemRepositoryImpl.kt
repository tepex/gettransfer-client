package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.PreferencesListener
import com.kg.gettransfer.data.SystemDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.SystemDataStoreCache
import com.kg.gettransfer.data.ds.SystemDataStoreRemote

import com.kg.gettransfer.data.model.ConfigsEntity
import com.kg.gettransfer.data.model.EndpointEntity
import com.kg.gettransfer.data.model.MobileConfigEntity
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.domain.repository.SystemRepository

import com.kg.gettransfer.sys.domain.MobileConfigs

import org.koin.core.get

class SystemRepositoryImpl(
    private val factory: DataStoreFactory<SystemDataStore, SystemDataStoreCache, SystemDataStoreRemote>
) : BaseRepository(), SystemRepository, PreferencesListener {

    private val preferencesCache = get<PreferencesCache>()

    init {
        preferencesCache.addListener(this)
    }

    override var configs = Configs.EMPTY
        private set

    override var mobileConfig = MobileConfigs.EMPTY
        private set

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

    override var accessToken: String
        get() = preferencesCache.accessToken
        set(value) {
            preferencesCache.accessToken = value
        }

    override var favoriteTransportTypes: Set<TransportType.ID>?
        get() = preferencesCache.favoriteTransportTypes
            ?.map { TransportType.ID.parse(it) }
            ?.toSet()
        set(value) {
            preferencesCache.favoriteTransportTypes = value?.map { it.name }?.toSet()
        }

    override suspend fun coldStart(): Result<Unit> {
        factory.retrieveRemoteDataStore().changeEndpoint(endpoint.map())
        if (mobileConfig === MobileConfigs.EMPTY) {
            val result: ResultEntity<MobileConfigEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getMobileConfigs()
            }
            if (result.error != null && result.entity == null) return Result(Unit, result.error.map())

            result.entity?.let { entity ->
                mobileConfig = entity.map()
                if (result.error == null) factory.retrieveCacheDataStore().setMobileConfigs(entity)
            }
        }

        if (configs === Configs.EMPTY) {
            val result: ResultEntity<ConfigsEntity?> = retrieveEntity { fromRemote ->
                factory.retrieveDataStore(fromRemote).getConfigs()
            }
            result.entity?.let { entity ->
                /* Save to cache only fresh data from remote */
                configs = entity.map()
                if (result.error == null) factory.retrieveCacheDataStore().setConfigs(entity)
            }
        }
        return Result(Unit)
    }

    override fun accessTokenChanged(accessToken: String) {}

    override fun endpointChanged(endpointEntity: EndpointEntity) {
        factory.retrieveRemoteDataStore().changeEndpoint(endpointEntity)
    }
}
