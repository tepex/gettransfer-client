package com.kg.gettransfer.modules.session


import android.util.Log
import com.kg.gettransfer.RxBus
import com.kg.gettransfer.network.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 01/02/2018.
 */


interface LoginCallback {
    fun onLogin()
    fun onFail(errorMessage: String)
}


enum class SessionState {
    LOGGED_OUT,
    LOGGED_IN
}


data class SessionEvent(val state: SessionState)


object SessionModule {
    private const val TAG = "SessionModule"


    private var sessionModel: SessionModel? = null

    val isLoggedIn: Boolean get() = sessionModel?.isLoggedIn ?: false

    var state: SessionState = SessionState.LOGGED_OUT
        set(v) {
            if (field != v) {
                field = v
                RxBus.publish(SessionEvent(v))
            }
        }


    // Blocks current thread
    fun getSession(): SessionModel? {
        if (sessionModel == null) {
            synchronized(this) {
                if (sessionModel == null) {
                    newSession()
                }
            }
        }
        return sessionModel
    }


    // Blocks current thread
    fun getAccessToken(): String? {
        return getSession()?.accessToken
    }


    // Blocks current thread
    private fun newSession() {
        Log.d(TAG, "getAccessToken()")
        val response = Api.api.getAccessToken("YES", Api.API_KEY).execute()
        if (response.isSuccessful) {
            val newAccessToken = response.body()?.data?.accessToken
            Log.d(TAG, "getAccessToken() responded success, new accessToken = $newAccessToken")
            if (newAccessToken != null) {
                sessionModel = SessionModel(newAccessToken)
                state = SessionState.LOGGED_OUT
            }
        }
    }


    // --


    fun login(email: String, pass: String, c: LoginCallback) {
        Api.api.login(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.ok) {
                        sessionModel?.email = email
                        c.onLogin()
                        state = SessionState.LOGGED_IN
                    } else c.onFail(it.data ?: "Unknown login error")
                }, {
                    c.onFail(it.message ?: "No network connection")
                })
    }
}


