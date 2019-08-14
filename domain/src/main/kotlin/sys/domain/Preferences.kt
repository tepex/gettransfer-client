package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.TransportType

data class Preferences(
    val accessToken: String,
    val endpoint: Endpoint?,
    val lastMode: String,
    val lastMainScreenMode: String,
    val lastCarrierTripsTypeView: String,
    val firstDayOfWeek: Int,
    val isFirstLaunch: Boolean,
    val isOnboardingShowed: Boolean,
    val selectedField: String,
    val addressHistory: List<GTAddress>,
    val favoriteTransports: Set<TransportType.ID>,
    val appEnters: Int,
    val isDebugMenuShowed: Boolean,
    val backgroundCoordinatesAccepted: Boolean?
) {

    fun isBackgroundCoordinatesAccepted() = backgroundCoordinatesAccepted != null && backgroundCoordinatesAccepted

    fun isBackgroundCoordinatesRejected() = backgroundCoordinatesAccepted != null && !backgroundCoordinatesAccepted

    companion object {
        const val INVALID_TOKEN = "invalid token"
        const val IMMUTABLE     = -1   // user did rate app
    }
}

