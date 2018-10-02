package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import android.util.Patterns

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.view.LoginView

import ru.terrakok.cicerone.Router

@InjectViewState
class LoginPresenter(cc: CoroutineContexts,
                     router: Router,
                     systemInteractor: SystemInteractor): BasePresenter<LoginView>(cc, router, systemInteractor) {


    companion object {
        @JvmField val RESULT_CODE = 33
        @JvmField val RESULT_OK = 1

        @JvmField val PARAM_KEY = "login_attempt"
    }

    private var email: String? = null
    private var password: String? = null

    fun onLoginClick() {
        viewState.blockInterface(false)
        utils.launchAsyncTryCatch({
            utils.asyncAwait { systemInteractor.login(email!!, password!!) }
            router.exitWithResult(RESULT_CODE, RESULT_OK)
            mFBA.logEvent(USER_EVENT,createBundle(PARAM_KEY, RESULT_SUCCESS))
        }, { e -> viewState.setError(e)
            mFBA.logEvent(USER_EVENT,createBundle(PARAM_KEY, RESULT_REJECTION))
        })
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
}
