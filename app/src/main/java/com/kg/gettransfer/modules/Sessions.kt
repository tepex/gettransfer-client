package com.kg.gettransfer.modules

import com.kg.gettransfer.network.Api
import com.kg.gettransfer.network.LoginPOJO
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class Sessions {
    interface LoginCallback {
        fun onLogin()
        fun onFail(errorMessage: String)
    }

    companion object {
        fun login(email: String, pass: String, c: LoginCallback) {
            Api.api.login(LoginPOJO(email, pass))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.ok) c.onLogin()
                        else c.onFail(it.data ?: "UNKNOWN ERROR")
                    }, {
                        c.onFail(it.message ?: "UNKNOWN ERROR")
                    })
        }
    }
}

