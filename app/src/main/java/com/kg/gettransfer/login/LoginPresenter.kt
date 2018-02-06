package com.kg.gettransfer.login


import com.kg.gettransfer.modules.session.SessionModule
import com.kg.gettransfer.modules.session.SessionState


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter : LoginContract.Presenter {
    override lateinit var view: LoginContract.View


    override val busy: Boolean get() = SessionModule.loggingIn.value


    override fun start() {
        view.showError(null)

        SessionModule.state.subscribe { if (it == SessionState.LOGGED_IN) view.loginSuccess() }
        SessionModule.loggingIn.subscribe { view.busyChanged(it) }
        SessionModule.errorsBus.subscribe { view.showError(it) }
    }


    override fun login(email: String, pass: String) {
        view.showError(null)
        SessionModule.login(email, pass)
    }
}