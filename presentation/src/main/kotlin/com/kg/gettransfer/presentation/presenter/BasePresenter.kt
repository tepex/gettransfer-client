package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.Analytics.Companion.BACK_CLICKED
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_MAIN
import com.kg.gettransfer.utilities.Analytics.Companion.PARAM_KEY_NAME

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import kotlinx.coroutines.Job

open class BasePresenter<BV: BaseView>: MvpPresenter<BV>(), KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val router: Router by inject()
    protected val analytics: Analytics by inject()
    protected val systemInteractor: SystemInteractor by inject()
    
    open fun onBackCommandClick() {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_NAME] = BACK_CLICKED

        router.exit()
        analytics.logEvent(EVENT_MAIN, createStringBundle(PARAM_KEY_NAME, BACK_CLICKED), map)
    }

    protected fun login(nextScreen: String, email: String) = router.navigateTo(Screens.Login(nextScreen, email))

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    companion object AnalyticProps {
        @JvmField val SINGLE_CAPACITY = 1
        @JvmField val DOUBLE_CAPACITY = 2

    }

    protected fun createStringBundle(key: String, value: String): Bundle {
        val bundle = Bundle(SINGLE_CAPACITY)
        bundle.putString(key, value)
        return bundle
    }
    
    protected fun createMultipleBundle(map: Map<String, Any>): Bundle {
        val bundle = Bundle()
        map.forEach { (k, v) -> bundle.putString(k, v.toString()) }
        return bundle
    }
}
