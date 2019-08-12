package com.kg.gettransfer.sys.domain

class IsShouldAskForRateInMarketInteractor(
    private val getPreferences: GetPreferencesInteractor
) {

    suspend operator fun invoke() = APP_ENTERS.contains(getPreferences().getModel().appEnters)

    companion object {
        private val APP_ENTERS = listOf(3, 9, 18)
    }
}
