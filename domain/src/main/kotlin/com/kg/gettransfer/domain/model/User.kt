package com.kg.gettransfer.domain.model

data class User(
    var profile: Profile,
    var termsAccepted: Boolean = false
) {

    val loggedIn: Boolean //is authorized user
        get() = profile.email != null && profile.phone != null

    val hasAccount: Boolean //is temporary or authorized user
        get() = (profile.email != null
                && profile.phone != null)
                || termsAccepted
}
