package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.http.HttpApi
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


    val isLoggedIn : Boolean get() = email != null
    val loggedIn = PublishRelay.create<Boolean>()
    val busy = BehaviorRelay.createDefault<Boolean>(false)
    val errorsBus = PublishRelay.create<String?>()


    // --


    init {
        email = session.email
        if (email != null) loggedIn.accept(true)

        session.state.subscribe {
            if (it.isLoggedIn) {
                val newEmail = session.email
                if (newEmail != email) {
                    email = newEmail
                    loggedIn.accept(true)
                }
            } else {
                email = null
                loggedIn.accept(false)
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
                        session.loggedIn(email)
                    } else {
                        errorsBus.accept(it.error?.message)
                    }
                }, {
                    busy.accept(false)
                    errorsBus.accept(it.localizedMessage)
                })
    }


    fun logOut() {
        session.logOut()
    }
}


