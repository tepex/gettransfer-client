package com.kg.gettransfer.sys.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Embedded
import androidx.room.PrimaryKey

import com.kg.gettransfer.sys.data.PreferencesEntity

import kotlinx.serialization.Serializable

@Entity(tableName = PreferencesEntity.ENTITY_NAME)
data class PreferencesModel(
    @ColumnInfo(name = PreferencesEntity.ACCESS_TOKEN) val accessToken: String,
    @Embedded(prefix = PreferencesEntity.ENDPOINT) val endpoint: EndpointModel?,
    @ColumnInfo(name = PreferencesEntity.IP_API_KEY) val ipApiKey: String?,
    @ColumnInfo(name = PreferencesEntity.IS_FIRST_LAUNCH) val isFirstLaunch: Boolean,
    @ColumnInfo(name = PreferencesEntity.IS_ONBOARDING_SHOWED) val isOnboardingShowed: Boolean,
    @ColumnInfo(name = PreferencesEntity.IS_NEW_DRIVER_APP_SHOWED) val isNewDriverAppDialogShowed: Boolean,
    @ColumnInfo(name = PreferencesEntity.COUNT_OF_SHOW_NEW_DRIVER_APP) val countOfShowNewDriverAppDialog: Int,
    @ColumnInfo(name = PreferencesEntity.SELECTED_FIELD) val selectedField: String,
    @ColumnInfo(name = PreferencesEntity.ADDRESS_HISTORY) val addressHistory: AddressHistoryList,
    @ColumnInfo(name = PreferencesEntity.FAVORITE_TRANSPORTS) val favoriteTransports: FavoriteTransportsList,
    @ColumnInfo(name = PreferencesEntity.APP_ENTERS) val appEnters: Int,
    @ColumnInfo(name = PreferencesEntity.IS_DEBUG_MENU_SHOWED) val isDebugMenuShowed: Boolean,

    @PrimaryKey(autoGenerate = true) val id: Long = 15
)

@Serializable
data class FavoriteTransportsList(val list: List<String>)

fun PreferencesModel.map() =
    PreferencesEntity(
        accessToken = accessToken,
        endpoint = endpoint?.map(),
        ipApiKey = ipApiKey,
        isFirstLaunch = isFirstLaunch,
        isOnboardingShowed = isOnboardingShowed,
        isNewDriverAppDialogShowed = isNewDriverAppDialogShowed,
        countOfShowNewDriverAppDialog = countOfShowNewDriverAppDialog,
        selectedField = selectedField,
        addressHistory = addressHistory.list.map { it.map() },
        favoriteTransports = favoriteTransports.list.toSet(),
        appEnters = appEnters,
        isDebugMenuShowed = isDebugMenuShowed
    )

fun PreferencesEntity.map() =
    PreferencesModel(
        accessToken = accessToken,
        endpoint = endpoint?.map(),
        ipApiKey = ipApiKey,
        isFirstLaunch = isFirstLaunch,
        isOnboardingShowed = isOnboardingShowed,
        isNewDriverAppDialogShowed = isNewDriverAppDialogShowed,
        countOfShowNewDriverAppDialog = countOfShowNewDriverAppDialog,
        selectedField = selectedField,
        addressHistory = AddressHistoryList(addressHistory.map { it.map() }),
        favoriteTransports = FavoriteTransportsList(favoriteTransports.toList()),
        appEnters = appEnters,
        isDebugMenuShowed = isDebugMenuShowed
    )
