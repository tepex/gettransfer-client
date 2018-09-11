package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Configs

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.ConfigsModel
import com.kg.gettransfer.presentation.view.BaseView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

open class BasePresenter<BV: BaseView>(protected val cc: CoroutineContexts,
                         protected val router: Router,
                         protected val apiInteractor: ApiInteractor): MvpPresenter<BV>() {
    protected val compositeDisposable = Job()
    protected val utils = AsyncUtils(cc)

    lateinit var configs: ConfigsModel
    lateinit var account: Account

//    fun onBackCommandClick() { viewState.finish() }
    fun onBackCommandClick() { router.exit() }

    protected fun login() { router.navigateTo(Screens.LOGIN) }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }
}
