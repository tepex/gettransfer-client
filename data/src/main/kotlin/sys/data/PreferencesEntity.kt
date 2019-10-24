package com.kg.gettransfer.sys.data

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.sys.domain.Preferences

data class PreferencesEntity(
    val accessToken: String,
    val endpoint: EndpointEntity?,
    val isFirstLaunch: Boolean,
    val isOnboardingShowed: Boolean,
    val isNewDriverAppDialogShowed: Boolean,
    val selectedField: String,
    val addressHistory: List<GTAddressEntity>,
    val favoriteTransports: Set<String>,
    val appEnters: Int,
    val isDebugMenuShowed: Boolean
) {

    companion object {
        const val ENTITY_NAME              = "preferences"
        const val ACCESS_TOKEN             = "access_token"
        const val ENDPOINT                 = "endpoint"
        const val IS_FIRST_LAUNCH          = "is_first_launch"
        const val IS_ONBOARDING_SHOWED     = "is_onboarding_showed"
        const val IS_NEW_DRIVER_APP_SHOWER = "is_new_driver_app_showed"
        const val SELECTED_FIELD           = "selected_field"
        const val ADDRESS_HISTORY          = "address_history"
        const val APP_ENTERS               = "app_enters"
        const val IS_DEBUG_MENU_SHOWED     = "is_debug_menu_showed"
        const val FAVORITE_TRANSPORTS      = "favorite_transports"
    }
}

fun PreferencesEntity.map() =
    Preferences(
        accessToken,
        endpoint?.map(),
        isFirstLaunch,
        isOnboardingShowed,
        isNewDriverAppDialogShowed,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed
    )

fun Preferences.map() =
    PreferencesEntity(
        accessToken,
        endpoint?.map(),
        isFirstLaunch,
        isOnboardingShowed,
        isNewDriverAppDialogShowed,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed
    )
