package com.kg.gettransfer.activity.login


import com.kg.gettransfer.modules.CurrentAccount
import io.reactivex.disposables.CompositeDisposable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class LoginPresenter(override val view: LoginContract.View) : LoginContract.Presenter, KoinComponent {
    private val currentAccount: CurrentAccount by inject()

    private val disposables = CompositeDisposable()


    override val busy: Boolean get() = currentAccount.busy


    init {
        view.showError(null)

        currentAccount.logOut()

        disposables.addAll(
                currentAccount.addOnAccountChanged {
                    if (currentAccount.loggedIn) view.loginSuccess()
                },
                currentAccount.addOnBusyChanged { view.busyChanged(it) },
                currentAccount.addOnError { view.showError(it.message) })
    }


    override fun login(email: String, pass: String) {
        view.showError(null)

        currentAccount.login(email, pass)
    }


    override fun stop() {
        if (currentAccount.busy) {
            currentAccount.logOut()
        }
        disposables.clear()
    }
}