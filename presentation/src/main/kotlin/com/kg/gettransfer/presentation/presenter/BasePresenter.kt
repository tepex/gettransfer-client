package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter
import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.yandex.metrica.YandexMetrica

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import kotlinx.coroutines.Job

open class BasePresenter<BV: BaseView>: MvpPresenter<BV>(), KoinComponent {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    protected val mFBA: FirebaseAnalytics by inject()
    protected val eventsLogger: AppEventsLogger by inject()
    protected val router: Router by inject()
    protected val systemInteractor: SystemInteractor by inject()
    
    open fun onBackCommandClick() {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_NAME] = SYSTEM_BACK_CLICKED

        router.exit()
        mFBA.logEvent(MainPresenter.EVENT_MAIN, createSingeBundle(PARAM_KEY_NAME, SYSTEM_BACK_CLICKED))
        eventsLogger.logEvent(MainPresenter.EVENT_MAIN, createSingeBundle(PARAM_KEY_NAME, SYSTEM_BACK_CLICKED))
        YandexMetrica.reportEvent(MainPresenter.EVENT_MAIN, map)
    }

    protected fun login(nextScreen: String, email: String) = router.navigateTo(Screens.Login(nextScreen, email))

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    companion object AnalyticProps {
        /** [см. табл.][https://docs.google.com/spreadsheets/d/1RP-96GhITF8j-erfcNXQH5kM6zw17ASmnRZ96qHvkOw/edit#gid=0] */
        @JvmField val RESULT_SUCCESS   = "success"
        @JvmField val RESULT_FAIL      = "fail"

        @JvmField val PARAM_KEY_NAME = "name"

        @JvmField val SINGLE_CAPACITY = 1
        @JvmField val DOUBLE_CAPACITY = 2

        @JvmField val SYSTEM_BACK_CLICKED = "back"
    }

    protected fun createSingeBundle(param: String, value: String): Bundle {
        val bundle = Bundle(SINGLE_CAPACITY)
        bundle.putString(param, value)
        return bundle
    }
    
    protected fun createMultipleBundle(map: Map<String, Any>): Bundle {
        val bundle = Bundle()
        map.forEach { (k,v) -> bundle.putString(k, v.toString()) }
        return bundle
    }
}
