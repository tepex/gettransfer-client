package com.kg.gettransfer.sys.data

import com.kg.gettransfer.data.model.GTAddressEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.sys.domain.Preferences

data class PreferencesEntity(
    val accessToken: String,
    val endpoint: EndpointEntity?,
    val lastMode: String,
    val lastMainScreenMode: String,
    val lastCarrierTripsTypeView: String,
    val firstDayOfWeek: Int,
    val isFirstLaunch: Boolean,
    val isOnboardingShowed: Boolean,
    val selectedField: String,
    val addressHistory: List<GTAddressEntity>,
    val favoriteTransports: Set<String>,
    val appEnters: Int,
    val isDebugMenuShowed: Boolean,
    val backgroundCoordinatesAccepted: Boolean?
) {

    companion object {
        const val ENTITY_NAME                     = "preferences"
        const val ACCESS_TOKEN                    = "access_token"
        const val ENDPOINT                        = "endpoint"
        const val LAST_MODE                       = "last_mode"
        const val LAST_MAIN_SCREEN_MODE           = "last_main_screen_mode"
        const val LAST_CARRIER_TRIPS_TYPE_VIEW    = "last_carrier_trips_type_view"
        const val FIRST_DAY_OF_WEEK               = "first_day_of_week"
        const val IS_FIRST_LAUNCH                 = "is_first_launch"
        const val IS_ONBOARDING_SHOWED            = "is_onboarding_showed"
        const val SELECTED_FIELD                  = "selected_field"
        const val ADDRESS_HISTORY                 = "address_history"
        const val APP_ENTERS                      = "app_enters"
        const val IS_DEBUG_MENU_SHOWED            = "is_debug_menu_showed"
        const val FAVORITE_TRANSPORTS             = "favorite_transports"
        const val BACKGROUND_COORDINATES_ACCEPTED = "background_coordinates_accepted"
    }
}

fun PreferencesEntity.map() =
    Preferences(
        accessToken,
        endpoint?.map(),
        lastMode,
        lastMainScreenMode,
        lastCarrierTripsTypeView,
        firstDayOfWeek,
        isFirstLaunch,
        isOnboardingShowed,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed,
        backgroundCoordinatesAccepted
    )

fun Preferences.map() =
    PreferencesEntity(
        accessToken,
        endpoint?.map(),
        lastMode,
        lastMainScreenMode,
        lastCarrierTripsTypeView,
        firstDayOfWeek,
        isFirstLaunch,
        isOnboardingShowed,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed,
        backgroundCoordinatesAccepted
    )
