package com.kg.gettransfer.login

import com.kg.gettransfer.modules.Sessions.Companion.login
import com.kg.gettransfer.modules.Sessions.LoginCallback


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter : LoginContract.Presenter {
    override lateinit var view: LoginContract.View

    override fun start() {

    }

    override fun login(email: String, pass: String) {
        login(
                "d.vakulenko@gmail.com",
                "keygkeyg",
                object : LoginCallback {
                    override fun onLogin() {
                        view.updateLoadingIndicator(false)
                    }
                    override fun onFail(errorMessage: String) {
                        view.updateLoadingIndicator(false)
                        view.showError(errorMessage)
                    }
                }
        )
    }
}