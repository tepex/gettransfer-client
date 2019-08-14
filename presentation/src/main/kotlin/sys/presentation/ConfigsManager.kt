package com.kg.gettransfer.sys.presentation

import com.kg.gettransfer.core.domain.Result

import com.kg.gettransfer.di.ENDPOINTS

import com.kg.gettransfer.sys.domain.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

import org.slf4j.Logger

class ConfigsManager : KoinComponent {

    lateinit var configs: Configs
        private set
    lateinit var mobile: MobileConfigs
        private set
    private lateinit var preferences: Preferences

    private val endpoints: List<Endpoint> by inject(named(ENDPOINTS))
    private val defaultEndpoint: Endpoint by inject()
    private val log: Logger by inject { parametersOf("ConfigsManager") }

    private val getPreferences: GetPreferencesInteractor by inject()
    private val getConfigs: GetConfigsInteractor by inject()
    private val getMobileConfigs: GetMobileConfigsInteractor by inject()
    private val setEndpoint: SetEndpointInteractor by inject()

    /** Get configs and mobileConfigs concurrently. */
    suspend fun coldStart(backgroundScope: CoroutineScope): Result.Failure<Any>? {
        /* We should await for preferences, cause we need to know endpoint to call network
           getMobileConfigs and getConfigs */
        preferences = backgroundScope.async { getPreferences() }.await().getModel()
        val endpoint = endpoints.find { it == preferences.endpoint } ?: defaultEndpoint
        if (preferences.endpoint == null) {
            preferences = preferences.copy(endpoint = defaultEndpoint)
        }
        backgroundScope.async { setEndpoint(endpoint) }.await()

        val mobileDeferred  = backgroundScope.async { getMobileConfigs() }
        val configsDeferred = backgroundScope.async { getConfigs() }

        val mobileResult = mobileDeferred.await().apply { mobile = getModel() }
        val configsResult = configsDeferred.await().apply { configs = getModel() }

        /* Return null on success or Result.Failure */
        return when {
            mobileResult  is Result.Failure -> mobileResult
            configsResult is Result.Failure -> configsResult
            else                            -> null
        }
    }

    /** Write preferences into DB */
    suspend fun apply() {
    }

    private suspend fun myDelay() {
        repeat(10) {
            delay(1000)
            log.debug("wait ${it+1}")
        }
    }
}
