package com.kg.gettransfer.domain.model

data class User(var profile: Profile, var termsAccepted: Boolean = true) {
    fun isLoggedIn() = profile.email != null
}
