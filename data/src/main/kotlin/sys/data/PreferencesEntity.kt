package com.kg.gettransfer.sys.data

import com.kg.gettransfer.core.data.GTAddressEntity
import com.kg.gettransfer.core.data.map
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.sys.domain.Preferences

data class PreferencesEntity(
    val endpoint: EndpointEntity?,
    val ipApiKey: String?,
    val isFirstLaunch: Boolean,
    val isOnboardingShowed: Boolean,
    val isNewDriverAppDialogShowed: Boolean,
    val countOfShowNewDriverAppDialog: Int,
    val selectedField: String,
    val addressHistory: List<GTAddressEntity>,
    val favoriteTransports: Set<String>,
    val appEnters: Int,
    val isDebugMenuShowed: Boolean,
    val isPaymentRequestWithoutDelay: Boolean
) {

    companion object {
        const val ENTITY_NAME                      = "preferences"
        const val ENDPOINT                         = "endpoint"
        const val IP_API_KEY                       = "ip_api_key"
        const val IS_FIRST_LAUNCH                  = "is_first_launch"
        const val IS_ONBOARDING_SHOWED             = "is_onboarding_showed"
        const val IS_NEW_DRIVER_APP_SHOWED         = "is_new_driver_app_showed"
        const val COUNT_OF_SHOW_NEW_DRIVER_APP     = "count_of_show_new_driver_app"
        const val SELECTED_FIELD                   = "selected_field"
        const val ADDRESS_HISTORY                  = "address_history"
        const val APP_ENTERS                       = "app_enters"
        const val IS_DEBUG_MENU_SHOWED             = "is_debug_menu_showed"
        const val FAVORITE_TRANSPORTS              = "favorite_transports"
        const val IS_PAYMENT_REQUEST_WITHOUT_DELAY = "is_payment_request_without_delay"
    }
}

fun PreferencesEntity.map() =
    Preferences(
        endpoint?.map(),
        ipApiKey,
        isFirstLaunch,
        isOnboardingShowed,
        isNewDriverAppDialogShowed,
        countOfShowNewDriverAppDialog,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed,
        isPaymentRequestWithoutDelay
    )

fun Preferences.map() =
    PreferencesEntity(
        endpoint?.map(),
        ipApiKey,
        isFirstLaunch,
        isOnboardingShowed,
        isNewDriverAppDialogShowed,
        countOfShowNewDriverAppDialog,
        selectedField,
        addressHistory.map { it.map() },
        favoriteTransports.map { it.map() }.toSet(),
        appEnters,
        isDebugMenuShowed,
        isPaymentRequestWithoutDelay
    )
