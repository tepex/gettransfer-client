package com.kg.gettransfer.login


import com.kg.gettransfer.modules.CurrentUser
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter(private val currentUser: CurrentUser) : LoginContract.Presenter, KoinComponent {
    override lateinit var view: LoginContract.View


    override val busy: Boolean get() = currentUser.busy.value


    override fun start() {
        view.showError(null)

        currentUser.state.subscribe { if (it == CurrentUser.SessionState.LOGGED_IN) view.loginSuccess() }
        currentUser.busy.subscribe { view.busyChanged(it) }
        currentUser.errorsBus.subscribe { view.showError(it) }
    }


    override fun login(email: String, pass: String) {
        view.showError(null)

        currentUser.login(email, pass)
    }
}