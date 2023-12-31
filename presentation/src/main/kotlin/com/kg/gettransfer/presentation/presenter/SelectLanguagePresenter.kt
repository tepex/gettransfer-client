package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import com.kg.gettransfer.core.presentation.WorkerManager
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectLanguageView
import com.kg.gettransfer.sys.domain.Configs
import com.kg.gettransfer.utilities.Analytics
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SelectLanguagePresenter : BasePresenter<SelectLanguageView>(), KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("SelectLanguagePresenter") }

    override fun attachView(view: SelectLanguageView) {
        super.attachView(view)

        worker.main.launch {
            val locales = configsManager.getConfigs().availableLocales
                .filter { Configs.LOCALES_FILTER.contains(it.language) }.map { it.map() }
            val selectedLanguage = sessionInteractor.locale.map()
            viewState.setLanguages(locales, selectedLanguage)
            viewState.showBottomSheet()
        }
    }

    fun changeLanguage(selected: LocaleModel) {
        worker.main.launch {
            sessionInteractor.locale = selected.delegate
            analytics.logEvent(Analytics.EVENT_SETTINGS, Analytics.LANGUAGE_PARAM, selected.name)
            saveGeneralSettings()
            viewState.recreateActivity()
        }
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
