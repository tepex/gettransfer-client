package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.MobileConfig
import com.kg.gettransfer.domain.model.Result

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
    val mobileConfig: MobileConfig

    suspend fun coldStart(): Result<Unit>
}
