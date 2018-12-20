package com.kg.gettransfer.domain.model

data class User(
    var profile: Profile,
    var termsAccepted: Boolean = true
) {

    val loggedIn: Boolean
        get() = profile.email != null
}
