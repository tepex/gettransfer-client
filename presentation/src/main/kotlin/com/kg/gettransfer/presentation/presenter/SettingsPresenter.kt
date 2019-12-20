package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.di.ENDPOINTS
import com.kg.gettransfer.domain.eventListeners.AccountChangedListener
import com.kg.gettransfer.domain.eventListeners.CreateTransferListener

import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SettingsView

import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.sys.domain.Endpoint
import com.kg.gettransfer.sys.domain.SetAccessTokenInteractor
import com.kg.gettransfer.sys.domain.SetDebugMenuShowedInteractor
import com.kg.gettransfer.sys.domain.SetEndpointInteractor
import com.kg.gettransfer.sys.domain.SetOnboardingShowedInteractor
import com.kg.gettransfer.sys.domain.SetNewDriverAppDialogShowedInteractor
import com.kg.gettransfer.sys.domain.GetPreferencesInteractor
import com.kg.gettransfer.sys.domain.ClearConfigsInteractor
import com.kg.gettransfer.sys.domain.ClearMobileConfigsInteractor

import com.kg.gettransfer.sys.presentation.EndpointModel
import com.kg.gettransfer.sys.presentation.map

import com.kg.gettransfer.utilities.Analytics

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

@Suppress("TooManyFunctions")
@InjectViewState
class SettingsPresenter : BasePresenter<SettingsView>(), AccountChangedListener, CreateTransferListener {

    private val endpoints: List<EndpointModel> = get<List<Endpoint>>(named(ENDPOINTS)).map { it.map() }

    private var restart = true

    private var count = 0
    private var startMillis = 0L

    private val worker: WorkerManager by inject { parametersOf("SettingsPresenter") }

    private val getPreferences: GetPreferencesInteractor by inject()
    private val setDebugMenuShowed: SetDebugMenuShowedInteractor by inject()
    private val setEndpoint: SetEndpointInteractor by inject()
    private val setOnboardingShowed: SetOnboardingShowedInteractor by inject()
    private val setAccessToken: SetAccessTokenInteractor by inject()
    private val setNewDriverAppDialogShowedInteractor: SetNewDriverAppDialogShowedInteractor by inject()

    private val clearConfigsInteractor: ClearConfigsInteractor by inject()
    private val clearMobileConfigsInteractor: ClearMobileConfigsInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        sessionInteractor.addCreateTransferListener(this)
    }

    override fun attachView(view: SettingsView) {
        super.attachView(view)
        sessionInteractor.addAccountChangedListener(this)
        if (restart) initConfigs()
        initDebugMenu()
        initSettings()
        checkLanguage()
    }

    /**
     * check language after login and if it difference then recreate activity
     * to change language for fragments
     */
    private fun checkLanguage() {
        if (accountManager.isLoggedIn) {
            val language = sessionInteractor.locale.language
            if (language != sessionInteractor.appLanguage) {
                sessionInteractor.appLanguage = language
                viewState.recreate()
            }
        }
    }

    private fun initSettings() {
        initGeneralSettings()
        initProfileSettings()
        worker.main.launch { viewState.hideSomeDividers() }
    }

    override fun detachView(view: SettingsView?) {
        super.detachView(view)
        sessionInteractor.removeAccountChangedListener(this)
    }

    override fun accountChanged() {
        worker.main.launch { initSettings() }
    }

    override fun onCreateTransferClick() {
        viewState.showOrderItem()
    }

    private fun initGeneralSettings() = worker.main.launch {
        viewState.initGeneralSettingsLayout()
        viewState.setCurrency(sessionInteractor.currency.map().name)
        val locale = sessionInteractor.locale
        val localeModel = configsManager.getConfigs().availableLocales
            .filter { Configs.LOCALES_FILTER.contains(it.language) }
            .map { it.map() }
            .find { it.delegate.language == locale.language } ?: LocaleModel(locale)
        viewState.setLocale(localeModel.name, locale.language)
        viewState.setDistanceUnit(sessionInteractor.distanceUnit == DistanceUnit.MI)
    }

    private fun initProfileSettings() = worker.main.launch {
        viewState.initProfileField(accountManager.isLoggedIn, accountManager.remoteProfile)
        if (accountManager.isLoggedIn) {
            setBalanceAndCreditLimit()
            viewState.setEmailNotifications(sessionInteractor.isEmailNotificationEnabled)
        } else {
            hideBalanceAndCreditLimit()
            viewState.hideEmailNotifications()
        }
    }

    private fun setBalanceAndCreditLimit() {
        with(accountManager.remoteAccount) {
            if (isBusinessAccount) {
                val balance = partner?.availableMoney?.default
                if (!balance.isNullOrEmpty()) viewState.setBalance(balance) else viewState.hideBalance()

                val creditLimit = partner?.creditLimit?.default
                if (!creditLimit.isNullOrEmpty()) viewState.setCreditLimit(creditLimit) else viewState.hideCreditLimit()
            } else {
                hideBalanceAndCreditLimit()
            }
        }
    }

    private fun hideBalanceAndCreditLimit() {
        viewState.hideBalance()
        viewState.hideCreditLimit()
    }

    private fun initDebugMenu() = worker.main.launch {
        val isDebugMenuShowed = withContext(worker.bg) { getPreferences().getModel() }.isDebugMenuShowed
        if (BuildConfig.FLAVOR == "dev" || isDebugMenuShowed) {
            showDebugMenu()
        }
    }

    private suspend fun showDebugMenu() {
        viewState.setEndpoints(endpoints)
        withContext(worker.bg) { getPreferences().getModel() }.endpoint?.let {
            viewState.setEndpoint(it.map())
        }
        viewState.showDebugMenu()
    }

    fun onDistanceUnitSwitched(isChecked: Boolean) {
        sessionInteractor.distanceUnit = when (isChecked) {
            true  -> DistanceUnit.MI
            false -> DistanceUnit.KM
        }.apply { analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.UNITS_PARAM, name) }
        worker.main.launch {
            saveGeneralSettings()
        }
    }

    fun onEmailNotificationSwitched(isChecked: Boolean) {
        sessionInteractor.isEmailNotificationEnabled = isChecked
        worker.main.launch {
            saveGeneralSettings()
        }
    }

    fun onAboutAppClicked() {
        val time = System.currentTimeMillis()

        // if it is the first time, or if it has been more than 3 seconds since the first tap
        // (so it is like a new try), we reset everything
        if (startMillis == 0L || time - startMillis > TIME_FOR_DEBUG) {
            startMillis = time
            count = 1
        } else {
            // it is not the first, and it has been  less than 3 seconds since the first
            count++
        }

        if (count == COUNTS_FOR_DEBUG) {
            count = 0
            switchDebugSettings()
        }
    }

    private fun switchDebugSettings() {
        if (BuildConfig.FLAVOR == "prod") {
            worker.main.launch {
                val isDebugMenuShowed = withContext(worker.bg) { getPreferences().getModel() }.isDebugMenuShowed
                if (isDebugMenuShowed) {
                    withContext(worker.bg) { setDebugMenuShowed(false) }
                    viewState.hideDebugMenu()
                } else {
                    withContext(worker.bg) { setDebugMenuShowed(true) }
                    showDebugMenu()
                }
            }
        }
    }

    fun changeEndpoint(selected: Int) = worker.main.launch {
        val endpoint = endpoints[selected]
        viewState.setEndpoint(endpoint)
        viewState.blockInterface(true)
        withContext(worker.bg) {
            clearConfigsInteractor()
            clearMobileConfigsInteractor()
            endpoint.delegate.run {
                setEndpoint(this)
            }
            clearAllCachedData()
        }
        configsManager.coldStart(worker.backgroundScope)
        fetchResult { sessionInteractor.coldStart() }
        viewState.blockInterface(false)
        restart = true
        router.exit() // Without restarting app
    }

    fun onResetOnboardingClicked() {
        worker.main.launch {
            withContext(worker.bg) { setOnboardingShowed(false) }
        }
    }

    fun onResetRateClicked() { reviewInteractor.shouldAskRateInMarket = true }

    fun onClearAccessTokenClicked() = worker.main.launch {
        withContext(worker.bg) { setAccessToken("") }
    }

    fun onResetNewDriverAppDialogClicked() = worker.main.launch {
        withContext(worker.bg) { setNewDriverAppDialogShowedInteractor(false) }
    }

    fun onCurrencyClicked() {
        if (!isBusinessAccount()) {
            viewState.showCurrencyChooser()
        }
    }

    fun onLanguageClicked() {
        viewState.showLanguageChooser()
    }

    fun onProfileFieldClicked() {
        if (accountManager.isLoggedIn) {
            router.navigateTo(Screens.ProfileSettings())
        } else {
            router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, null))
        }
    }

    fun onShareClick() {
        log.debug("Share action")
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.SHARE)
        router.navigateTo(Screens.Share())
    }

    private fun initConfigs() {
        restart = false
    }

    fun onForceCrashClick() {
        error("This is force crash")
    }

    override fun onDestroy() {
        worker.cancel()
        sessionInteractor.removeCreateTransferListener(this)
        super.onDestroy()
    }

    companion object {
        private const val COUNTS_FOR_DEBUG = 7
        private const val TIME_FOR_DEBUG = 3000L
    }
}
