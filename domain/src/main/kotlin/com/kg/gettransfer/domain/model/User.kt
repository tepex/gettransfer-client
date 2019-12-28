package com.kg.gettransfer.domain.model

data class User(
    var profile: Profile, /* TODO change to `val` */
    var termsAccepted: Boolean /* TODO change to `val` */
) {

    companion object {
        val EMPTY = User(Profile.EMPTY.copy(), false)
    }
}
