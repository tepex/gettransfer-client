package com.kg.gettransfer.login


import com.kg.gettransfer.modules.CurrentAccount
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter(private val currentAccount: CurrentAccount) : LoginContract.Presenter, KoinComponent {
    override lateinit var view: LoginContract.View


    override val busy: Boolean get() = currentAccount.busy.value


    override fun start() {
        view.showError(null)

        currentAccount.logOut()

        currentAccount.loggedIn.subscribe { if (it) view.loginSuccess() }
        currentAccount.busy.subscribe { view.busyChanged(it) }
        currentAccount.errorsBus.subscribe { view.showError(it) }
    }


    override fun login(email: String, pass: String) {
        view.showError(null)

        currentAccount.login(email, pass)
    }
}