package com.kg.gettransfer.sys.presentation

import com.kg.gettransfer.core.domain.Result
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.di.ENDPOINTS
import com.kg.gettransfer.di.IP_API_KEY

import com.kg.gettransfer.sys.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

import org.slf4j.Logger

class ConfigsManager : KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("ConfigsManager") }

    private val endpoints: List<Endpoint> by inject(named(ENDPOINTS))
    private val defaultEndpoint: Endpoint by inject()
    private val ipApiKey: String by inject(named(IP_API_KEY))

    private val log: Logger by inject { parametersOf("ConfigsManager") }

    private val getPreferences: GetPreferencesInteractor by inject()
    private val getConfigsInteractor: GetConfigsInteractor by inject()
    private val getMobileConfigsInteractor: GetMobileConfigsInteractor by inject()
    private val setEndpoint: SetEndpointInteractor by inject()
    private val setIpApiKey: SetIpApiKeyInteractor by inject()

    suspend fun getConfigs() = withContext(worker.bg) { getConfigsInteractor().getModel() }

    suspend fun getMobileConfigs() = withContext(worker.bg) { getMobileConfigsInteractor().getModel() }

    /** Get configs and mobileConfigs concurrently. */
    suspend fun coldStart(backgroundScope: CoroutineScope): Result.Failure<Any>? {
        /* We should await for preferences, cause we need to know endpoint to call network
           getMobileConfigs and getConfigs */
        val preferences = backgroundScope.async { getPreferences() }.await().getModel()
        val endpoint = endpoints.find { it == preferences.endpoint } ?: defaultEndpoint

        backgroundScope.async { setEndpoint(endpoint) }.await()
        backgroundScope.async { setIpApiKey(ipApiKey) }.await()

        val mobileResult  = backgroundScope.async { getMobileConfigsInteractor() }.await()
        val configsResult = backgroundScope.async { getConfigsInteractor() }.await()

        /* Return null on success or Result.Failure */
        return when {
            mobileResult  is Result.Failure -> mobileResult
            configsResult is Result.Failure -> configsResult
            else                            -> null
        }
    }

    /** Write preferences into DB */
    suspend fun apply() {}

    private suspend fun myDelay() {
        repeat(MY_DELAY_REPEAT_TIMES) { index ->
            delay(MY_DELAY_MILLIS)
            log.debug("wait ${index + 1}")
        }
    }

    companion object {
        const val MY_DELAY_REPEAT_TIMES = 10
        const val MY_DELAY_MILLIS = 1000L
    }
}
