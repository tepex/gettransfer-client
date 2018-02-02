package com.kg.gettransfer.modules

import android.util.Log
import com.kg.gettransfer.models.Session
import com.kg.gettransfer.network.Api
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 01/02/2018.
 */


interface Sessions {
    interface LoginCallback {
        fun onLogin()
        fun onFail(errorMessage: String)
    }

    companion object {
        private const val TAG = "Sessions"


        private var _session: Session? = null


        // Blocks current thread if uninitialized or expired
        // Call only from bg thread!
        val session: Session?
            get() {
                if (_session == null) {
                    // TODO: BULLSHIT, another var for sync
                    synchronized(authorized) {
                        if (_session == null) {
                            _session = newSession()
                        }
                    }
                }
                return _session
            }


        val authorized: Boolean get() = _session?.authorized ?: false


        // Blocks current thread
        private fun newSession(): Session? {
            Log.d(TAG, "getAccessToken()")

            val response = Api.api.getAccessToken("YES", Api.API_KEY).execute()
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.data?.accessToken
                Log.d(TAG, "getAccessToken() responded success, new accessToken = $newAccessToken")
                if (newAccessToken != null) return Session(newAccessToken)
            }

            return null
        }


        fun login(email: String, pass: String, c: LoginCallback) {
            Api.api.login(email, pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.ok) {
                            // TODO: Drop DB here
                            session?.email = email
                            c.onLogin()
                        } else c.onFail(it.data ?: "Unknown login error")
                    }, {
                        c.onFail(it.message ?: "No network connection")
                    })
        }
    }
}

