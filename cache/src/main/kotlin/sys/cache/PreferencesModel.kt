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
    @ColumnInfo(name = PreferencesEntity.IS_FIRST_LAUNCH) val isFirstLaunch: Int,
    @ColumnInfo(name = PreferencesEntity.IS_ONBOARDING_SHOWED) val isOnboardingShowed: Int,
    @ColumnInfo(name = PreferencesEntity.SELECTED_FIELD) val selectedField: String,
    @ColumnInfo(name = PreferencesEntity.ADDRESS_HISTORY) val addressHistory: AddressHistoryList,
    @ColumnInfo(name = PreferencesEntity.FAVORITE_TRANSPORTS) val favoriteTransports: FavoriteTransportsList,
    @ColumnInfo(name = PreferencesEntity.APP_ENTERS) val appEnters: Int,
    @ColumnInfo(name = PreferencesEntity.IS_DEBUG_MENU_SHOWED) val isDebugMenuShowed: Int,

    @PrimaryKey(autoGenerate = true) val id: Long = 15
)

@Serializable
data class FavoriteTransportsList(val list: List<String>)

fun PreferencesModel.map() =
    PreferencesEntity(
        accessToken = accessToken,
        endpoint = endpoint?.map(),
        isFirstLaunch = if (isFirstLaunch == 1) true else false,
        isOnboardingShowed = if (isOnboardingShowed == 1) true else false,
        selectedField = selectedField,
        addressHistory = addressHistory.list.map { it.map() },
        favoriteTransports = favoriteTransports.list.toSet(),
        appEnters = appEnters,
        isDebugMenuShowed = if (isDebugMenuShowed == 1) true else false
    )

fun PreferencesEntity.map() =
    PreferencesModel(
        accessToken = accessToken,
        endpoint = endpoint?.map(),
        isFirstLaunch = if (isFirstLaunch) 1 else 0,
        isOnboardingShowed = if (isOnboardingShowed) 1 else 0,
        selectedField = selectedField,
        addressHistory = AddressHistoryList(addressHistory.map { it.map() } ),
        favoriteTransports = FavoriteTransportsList(favoriteTransports.toList()),
        appEnters = appEnters,
        isDebugMenuShowed = if (isDebugMenuShowed) 1 else 0
    )
