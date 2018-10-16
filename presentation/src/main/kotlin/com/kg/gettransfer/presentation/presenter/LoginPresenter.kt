package com.kg.gettransfer.presentation.presenter

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
                     systemInteractor: SystemInteractor): BaseLoadingPresenter<LoginView>(cc, router, systemInteractor) {


    companion object {
        @JvmField val RESULT_CODE = 33
        @JvmField val RESULT_OK = 1

        @JvmField val EVENT = "login"
        @JvmField val PARAM_KEY = "status"
    }

    private var email: String? = null
    private var password: String? = null

    fun onLoginClick() {
        viewState.blockInterface(false)
        utils.launchAsyncTryCatchFinally({
            viewState.showLoading()
            utils.asyncAwait { systemInteractor.login(email!!, password!!) }
            router.exitWithResult(RESULT_CODE, RESULT_OK)
            mFBA.logEvent(EVENT,createSingeBundle(PARAM_KEY, RESULT_SUCCESS))
        }, { e -> viewState.setError(e)
            mFBA.logEvent(EVENT,createSingeBundle(PARAM_KEY, RESULT_FAIL))
        }, { viewState.hideLoading() })
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
