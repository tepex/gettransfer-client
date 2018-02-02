package com.kg.gettransfer.login

import com.kg.gettransfer.modules.Sessions.Companion.login
import com.kg.gettransfer.modules.Sessions.LoginCallback


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter : LoginContract.Presenter {
    override lateinit var view: LoginContract.View

    var _busy: Boolean = false
        set(v) {
            view.busyChanged(v)
        }
    override val busy: Boolean = _busy

    override fun start() {
        _busy = false
        view.showError(null)
    }

    override fun login(email: String, pass: String) {
        _busy = true
        view.showError(null)
        login(
                "d.vakulenko@gmail.com",
                "keygkeyg",
                object : LoginCallback {
                    override fun onLogin() {
                        _busy = false
                    }

                    override fun onFail(errorMessage: String) {
                        _busy = false
                        view.showError(errorMessage)
                    }
                }
        )
    }
}