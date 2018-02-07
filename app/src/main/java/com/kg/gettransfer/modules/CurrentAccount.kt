package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.network.HttpApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 05/02/2018.
 */


class CurrentAccount(val session: Session, val api: HttpApi) : KoinComponent {
    var email: String? = null
        private set

    var phone: String? = null
    var sign: String? = null


    // --


    enum class SessionState {
        LOGGED_OUT,
        LOGGED_IN
    }

    val state = BehaviorRelay.createDefault<SessionState>(SessionState.LOGGED_OUT)
    val busy = BehaviorRelay.createDefault<Boolean>(false)
    val errorsBus = PublishRelay.create<String?>()


    // --


    init {
        session.state.subscribe {
            if (it.isLoggedIn) {
                val newEmail = session.email
                if (newEmail != email) {
                    email = newEmail
                    state.accept(SessionState.LOGGED_IN)
                }
            } else {
                email = null
                state.accept(SessionState.LOGGED_OUT)
            }
        }
    }


    fun login(email: String, pass: String) {
        if (busy.value) return
        api.login(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ busy.accept(true) })
                .subscribe({
                    busy.accept(false)
                    if (it.ok) {
                        session.loggedin(email)
                    } else {
                        errorsBus.accept(it.error?.message)
                    }
                }, {
                    busy.accept(false)
                    errorsBus.accept(it.localizedMessage)
                })
    }
}