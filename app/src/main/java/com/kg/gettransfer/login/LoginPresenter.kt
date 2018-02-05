package com.kg.gettransfer.login


import com.kg.gettransfer.modules.session.LoginCallback
import com.kg.gettransfer.modules.session.SessionModule


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter : LoginContract.Presenter {
    override lateinit var view: LoginContract.View


    var _busy: Boolean = false
        set(v) {
            if (field != v) {
                field = v
                view.busyChanged(v)
            }
        }

    override val busy: Boolean get() = _busy


    override fun start() {
        _busy = false
        view.showError(null)
    }


    override fun login(email: String, pass: String) {
        _busy = true
        view.showError(null)
        SessionModule.login(
                email,
                pass,
                object : LoginCallback {
                    override fun onLogin() {
                        _busy = false
                        view.loginSuccess()
                    }

                    override fun onFail(errorMessage: String) {
                        _busy = false
                        view.showError(errorMessage)
                    }
                }
        )
    }
}