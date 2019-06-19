package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.mapper.EndpointMapper
import com.kg.gettransfer.data.mapper.AddressMapper
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.repository.SystemRepository

import org.koin.standalone.get

class SystemRepositoryImpl : BaseRepository(), SystemRepository{

    private val preferencesCache = get<PreferencesCache>()
    private val endpointMapper   = get<EndpointMapper>()
    private val addressMapper    = get<AddressMapper>()

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

    override val endpoints = preferencesCache.endpoints.map { endpointMapper.fromEntity(it) }

    override var endpoint: Endpoint
        get() = endpointMapper.fromEntity(preferencesCache.endpoint)
        set(value) {
            val endpointEntity = endpointMapper.toEntity(value)
            preferencesCache.endpoint = endpointEntity
        }

    override var addressHistory: List<GTAddress>
        get() = preferencesCache.addressHistory.map { addressMapper.fromEntity(it) }
        set(value) { preferencesCache.addressHistory = value.map { addressMapper.toEntity(it) } }

    override var appEnters: Int
        get() = preferencesCache.appEnters
        set(value) { preferencesCache.appEnters = value }

    override var isDebugMenuShowed: Boolean
        get() = preferencesCache.isDebugMenuShowed
        set(value) { preferencesCache.isDebugMenuShowed = value }
}
