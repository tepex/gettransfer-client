package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.TransportType

data class Preferences(
    val accessToken: String,
    val endpoint: Endpoint?,
    val isFirstLaunch: Boolean,
    val isOnboardingShowed: Boolean,
    val isNewDriverAppDialogShowed: Boolean,
    val countOfShowNewDriverAppDialog: Int,
    val selectedField: String,
    val addressHistory: List<GTAddress>,
    val favoriteTransports: Set<TransportType.ID>,
    val appEnters: Int,
    val isDebugMenuShowed: Boolean
) {

    companion object {
        const val IMMUTABLE = -1   // user did rate app
    }
}
