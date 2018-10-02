package com.kg.gettransfer.presentation.presenter

import android.os.Build
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

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

open class BasePresenter<BV: BaseView>(protected val cc: CoroutineContexts,
                         protected val router: Router,
                         protected val systemInteractor: SystemInteractor): MvpPresenter<BV>() {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(cc, compositeDisposable)
    protected val mFBA: FirebaseAnalytics by inject()

    open fun onBackCommandClick() = router.exit()

    protected fun login() = router.navigateTo(Screens.LOGIN)

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }


    companion object AnalyticProps{
        const val USER_EVENT = "user_action"

        const val RESULT_SUCCESS = "success"
        const val RESULT_REJECTION= "rejection"

        const val SINGLE_CAPACITY = 1


    }

    protected fun createBundle(param: String,value: String):Bundle {
        val bundle = Bundle(SINGLE_CAPACITY)
        bundle.putString(param, value)
        return bundle
    }
}
