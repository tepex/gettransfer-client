package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.presentation.view.SelectLanguageView
import com.kg.gettransfer.sys.domain.Configs

import com.kg.gettransfer.sys.presentation.ConfigsManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class SelectLanguagePresenter : MvpPresenter<SelectLanguageView>(), KoinComponent {

    private val worker: WorkerManager by inject { parametersOf("SelectLanguagePresenter") }
    private val sessionInteractor: SessionInteractor by inject()
    private val configsManager: ConfigsManager by inject()

    override fun attachView(view: SelectLanguageView?) {
        super.attachView(view)

        worker.main.launch {
            val locales = withContext(worker.bg) {
                configsManager.configs.availableLocales.filter { Configs.LOCALES_FILTER.contains(it.language) }
                        .map { it.map() }
            }
            val selectedLanguage = sessionInteractor.locale.map()
            viewState.setLanguages(locales, selectedLanguage)
        }
    }

    fun changeLanguage(selected: LocaleModel) {
        sessionInteractor.locale = selected.delegate
        viewState.sendEvent(selected)
    }

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }
}
