package com.kg.gettransfer.modules


import android.content.SharedPreferences
import com.jakewharton.rxrelay2.PublishRelay
import org.koin.standalone.KoinComponent


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class Session(val sp: SharedPreferences) : KoinComponent {
    @Volatile
    var accessToken: String? = null

    @Volatile
    var email: String? = null

    val hasToken: Boolean get() = accessToken != null
    val isLoggedIn: Boolean get() = email != null

    val state: PublishRelay<Session> = PublishRelay.create()


    init {
        accessToken = sp.getString("Session.accessToken", null)
        email = sp.getString("Session.email", null)
    }


    private fun save() {
        sp.edit()
                .putString("Session.accessToken", accessToken)
                .putString("Session.email", email)
                .apply()
    }


    fun reset(newAccessToken: String) {
        accessToken = newAccessToken
        email = null
        save()
        state.accept(this)
    }


    fun loggedIn(email: String) {
        this.email = email
        save()
        state.accept(this)
    }


    fun logOut() {
        accessToken = null
        email = null
        save()
        state.accept(this)
    }
}


