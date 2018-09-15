package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

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

    fun onBackCommandClick() = router.exit()

    protected fun login() = router.navigateTo(Screens.LOGIN)

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}
