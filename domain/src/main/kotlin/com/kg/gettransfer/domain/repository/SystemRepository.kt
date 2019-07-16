package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Configs
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.sys.domain.MobileConfigs

interface SystemRepository {
    val endpoints: List<Endpoint>

    var lastMode: String
    var lastMainScreenMode: String
    var lastCarrierTripsTypeView: String
    var firstDayOfWeek: Int
    var isFirstLaunch: Boolean
    var isOnboardingShowed: Boolean
    var selectedField: String
    var endpoint: Endpoint
    var addressHistory: List<GTAddress>
    var appEnters: Int
    var isDebugMenuShowed: Boolean
    val mobileConfig: MobileConfigs
    var accessToken: String
    val configs: Configs
    var favoriteTransportTypes: Set<TransportType.ID>?

    suspend fun coldStart(): Result<Unit>
}
