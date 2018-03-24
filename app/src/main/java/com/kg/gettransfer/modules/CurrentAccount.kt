package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.BehaviorRelay
import com.kg.gettransfer.modules.http.HttpApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 05/02/2018.
 */


// Singleton
class CurrentAccount(
        private val session: Session,
        private val api: HttpApi)
    : AsyncModel(), KoinComponent {

    private val brEmail = BehaviorRelay.createDefault<String>(session.email)!!

    var email: String
        get() = brEmail.value
        private set(v) = brEmail.accept(v)

    val loggedIn: Boolean get() = email.isNotEmpty()

    fun addOnAccountChanged(f: ((String) -> Unit)): Disposable {
        val d = brEmail.observeOn(AndroidSchedulers.mainThread()).subscribe(f)
        disposables.add(d)
        return d
    }


    // --


    init {
        session.state.subscribe {
            val newEmail = session.email
            if (newEmail != email) {
                email = newEmail
            }
        }
    }


    fun login(email: String, pass: String): Boolean {
        if (busy) return false
        api.login(email, pass).fastSubscribe { session.loggedIn(email) }
        return true
    }


    fun logOut() {
        session.logOut()
    }
}


