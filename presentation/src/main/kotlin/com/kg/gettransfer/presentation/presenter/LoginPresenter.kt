package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Patterns

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class LoginPresenter(cc: CoroutineContexts, 
                     private val router: Router,
                     private val apiInteractor: ApiInteractor): MvpPresenter<LoginView>() {

    companion object {
        @JvmField val RESULT_CODE = 33
        @JvmField val RESULT_OK = 1
    }
    
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)
    
    private var email: String? = null
    private var password: String? = null
    
    fun onLoginClick() {
        viewState.blockInterface(false)
        utils.launchAsyncTryCatch(compositeDisposable, {
            val account = utils.asyncAwait { apiInteractor.login(email!!, password!!) }
            router.exitWithResult(RESULT_CODE, RESULT_OK)
        }, { e -> viewState.setError(false, R.string.err_server, e.message) })
    }
    
    fun setEmail(email: String) {
        if(email.isEmpty()) this.email = null else this.email = email
        checkFields()
    }

    fun setPassword(password: String) {
        if(password.isEmpty()) this.password = null else this.password = password
        checkFields()
    }
    
    private fun checkFields() {
        val checkEmail = email != null && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
        val checkPassword = password != null && password!!.length >= 6
        viewState.enableBtnLogin(checkEmail && checkPassword)
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
