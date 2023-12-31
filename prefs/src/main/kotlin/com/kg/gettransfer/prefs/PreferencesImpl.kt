package com.kg.gettransfer.prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.domain.model.Account

@Suppress("WildcardImport")
class PreferencesImpl(
    context: Context,
    private val defaultUrl: String
) : PreferencesCache {

    private val configsPrefs  = context.getSharedPreferences("configs", Context.MODE_PRIVATE)
    private var _accessToken: String? = null

    private var _endpointUrl: String? = null

    private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type

    override var endpointUrl: String
        get() {
            if (_endpointUrl == null) {
                _endpointUrl = configsPrefs.getString(ENDPOINT_URL, defaultUrl)
            }
            @Suppress("UnsafeCallOnNullableType")
            return _endpointUrl!!
        }
        set(value) {
            _endpointUrl = value
            with(configsPrefs.edit()) {
                putString(ENDPOINT_URL, value)
                apply()
            }
        }

    override var accessToken: String
        get() {
            if (_accessToken.isNullOrEmpty()) {
                _accessToken = configsPrefs.getString(ACCESS_TOKEN, EMPTY)
            }
            return _accessToken ?: EMPTY
        }
        set(value) {
            _accessToken = value
            configsPrefs.edit().putString(ACCESS_TOKEN, value).apply()
        }

    override var mapCountNewOffers: Map<Long, Int>
        get() = getMap(MAP_NEW_OFFERS)
        set(value) {
            setMap(value, MAP_NEW_OFFERS)
        }

    override var mapCountNewMessages: Map<Long, Int>
        get() = getMap(MAP_NEW_MESSAGES)
        set(value) {
            setMap(value, MAP_NEW_MESSAGES)
        }

    override var mapCountViewedOffers: Map<Long, Int>
        get() = getMap(MAP_VIEWED_MESSAGES)
        set(value) {
            setMap(value, MAP_VIEWED_MESSAGES)
        }

    override var eventsCount: Int
        get() = configsPrefs.getInt(EVENTS_COUNT, 0)
        set(value) {
            configsPrefs.edit().putInt(EVENTS_COUNT, value).apply()
        }

    override var appLanguage: String
        get() = configsPrefs.getString(APP_LANGUAGE, Account.EMPTY.locale.language).toString()
        set(value) { configsPrefs.edit().putString(APP_LANGUAGE, value).apply() }

    override var isAppLanguageChanged: Boolean
        get() = configsPrefs.getBoolean(APP_LANGUAGE_CHANGED, false)
        set(value) {
            with(configsPrefs.edit()) {
                // need to use commit() instead apply() in order to save value after restarting app.
                // otherwise value won't be updated
                // see this problem here https://gettransfercom.atlassian.net/browse/GAA-1799
                putBoolean(APP_LANGUAGE_CHANGED, value).commit()
            }
        }

    private fun getMap(key: String): Map<Long, Int> {
        val json = configsPrefs.getString(key, null)
        return if (json != null) Gson().fromJson(json, genericType<Map<Long, Int>>()) else emptyMap()
    }

    private fun setMap(value: Map<Long, Int>, key: String) {
        configsPrefs.edit().putString(key, Gson().toJson(value)).apply()
    }

    override fun logout() {
        _accessToken  = null
        with(configsPrefs.edit()) {
            remove(ACCESS_TOKEN)
            apply()
        }
    }

    companion object {
        const val ACCESS_TOKEN        = "token"
        const val ENDPOINT_URL        = "endpoint"
        const val APP_LANGUAGE        = "app_language"
        const val APP_LANGUAGE_CHANGED = "app_language_changed"

        const val MAP_NEW_OFFERS      = "map_new_offers"
        const val MAP_NEW_MESSAGES    = "map_new_messages"
        const val MAP_VIEWED_MESSAGES = "map_viewed_messages"
        const val EVENTS_COUNT        = "count_new_offers"

        const val EMPTY = ""
    }
}
