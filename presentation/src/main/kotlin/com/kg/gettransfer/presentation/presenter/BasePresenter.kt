package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.support.annotation.CallSuper
import com.arellomobile.mvp.MvpPresenter
import com.google.firebase.analytics.FirebaseAnalytics
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.extensions.inject
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.BaseView
import ru.terrakok.cicerone.Router
import kotlinx.coroutines.Job

open class BasePresenter<BV: BaseView>(protected val cc: CoroutineContexts,
                                       protected val router: Router,
                                       protected val systemInteractor: SystemInteractor): MvpPresenter<BV>() {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(cc, compositeDisposable)
    protected val mFBA: FirebaseAnalytics by inject()

    open fun onBackCommandClick() {
        router.exit()
        mFBA.logEvent(MainPresenter.EVENT_MAIN, createSingeBundle(PARAM_KEY_NAME, SYSTEM_BACK_CLICKED))
    }

    protected fun login() = router.navigateTo(Screens.LOGIN)

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

    fun changeNetworkState(isNetworkAvailable: Boolean){
        systemInteractor.changeNetworkAvailability(isNetworkAvailable)
    }
}
