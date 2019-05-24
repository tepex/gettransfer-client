package com.kg.gettransfer.domain.model

data class User(
    var profile: Profile,
    var termsAccepted: Boolean = false
) {

    val loggedIn: Boolean //is authorized user
        get() = (!profile.email.isNullOrEmpty() || !profile.phone.isNullOrEmpty()) && termsAccepted

    val hasAccount: Boolean //is temporary or authorized user
        get() = loggedIn || (profile.email.isNullOrEmpty() && profile.email.isNullOrEmpty() && termsAccepted)
}
