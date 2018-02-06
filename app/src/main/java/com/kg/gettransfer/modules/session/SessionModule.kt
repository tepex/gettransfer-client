package com.kg.gettransfer.modules.session


import android.util.Log
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.kg.gettransfer.modules.network.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 01/02/2018.
 */


enum class SessionState {
    LOGGED_OUT,
    LOGGED_IN
}


data class SessionEvent(val state: SessionState)


object SessionModule {
    private const val TAG = "SessionModule"


    var sessionModel: SessionModel? = null
        private set
        get() { // Blocks current thread
            if (field == null) {
                synchronized(this) {
                    if (field == null) {
                        newSession()
                    }
                }
            }
            return field
        }

    val isLoggedIn: Boolean get() = sessionModel?.isLoggedIn ?: false


    val state = BehaviorRelay.createDefault<SessionState>(SessionState.LOGGED_OUT)
    val loggingIn = BehaviorRelay.createDefault<Boolean>(false)
    val errorsBus = PublishRelay.create<String?>()


    // Blocks current thread
    fun getAccessToken(): String? {
        return sessionModel?.accessToken
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
                state.accept(SessionState.LOGGED_OUT)
            }
        }
    }


    // --


    fun login(email: String, pass: String) {
        if (loggingIn.value) return
        Api.api.login(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ loggingIn.accept(true) })
                .subscribe({
                    loggingIn.accept(false)
                    if (it.ok) {
                        sessionModel?.email = email
                        state.accept(SessionState.LOGGED_IN)
                    } else {
                        errorsBus.accept(it.error?.message)
                    }
                }, {
                    loggingIn.accept(false)
                    errorsBus.accept(it.localizedMessage)
                })
    }
}


