package com.kg.gettransfer.modules.session


/**
 * Created by denisvakulenko on 01/02/2018.
 */


class SessionModel(
        var accessToken: String,
        var email: String? = null
) {

    val isLoggedIn: Boolean get() = email != null
}