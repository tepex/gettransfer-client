package com.kg.gettransfer.login


import com.kg.gettransfer.modules.CurrentAccount
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter(private val currentAccount: CurrentAccount) : LoginContract.Presenter, KoinComponent {
    override lateinit var view: LoginContract.View

    private val disposables = CompositeDisposable()

    override val busy: Boolean get() = currentAccount.busy.value


    override fun start() {
        view.showError(null)

        currentAccount.logOut()

        disposables.add(
                currentAccount.loggedIn.subscribe { if (it) view.loginSuccess() })
        disposables.add(
                currentAccount.busy.subscribe { view.busyChanged(it) })
        disposables.add(
                currentAccount.errorsBus.subscribe { view.showError(it) })
    }


    override fun login(email: String, pass: String) {
        view.showError(null)

        currentAccount.login(email, pass)
    }


    override fun stop() {
        disposables.clear()
    }
}