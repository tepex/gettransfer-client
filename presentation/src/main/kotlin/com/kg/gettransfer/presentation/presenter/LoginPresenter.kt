package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.coroutines.experimental.Job

import timber.log.Timber

@InjectViewState
class LoginPresenter(cc: CoroutineContexts, private val apiInteractor: ApiInteractor): MvpPresenter<LoginView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    fun onLoginClick(email: String, password: String) {
        utils.launchAsyncTryCatch(compositeDisposable, {
            viewState.showError(false)
            val account = utils.asyncAwait { apiInteractor.login(email, password) }
            Timber.d("account: %s", account)
            viewState.finish()
        }, { e ->
            Timber.e(e)
            viewState.showError(true)
        })
    }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}
