package com.kg.gettransfer.modules


import com.jakewharton.rxrelay2.PublishRelay
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class Session : KoinComponent {
    @Volatile
    var accessToken: String? = null

    @Volatile
    var email: String? = null

    val hasToken: Boolean get() = accessToken != null
    val isLoggedIn: Boolean get() = email != null

    val state: PublishRelay<Session> = PublishRelay.create()

    fun reset(newAccessToken: String) {
        accessToken = newAccessToken
        email = null
        state.accept(this)
    }

    fun loggedin(email: String) {
        this.email = email
        state.accept(this)
    }
}


