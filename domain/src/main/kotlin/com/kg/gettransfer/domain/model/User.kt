package com.kg.gettransfer.domain.model

data class User(
    val profile: Profile,
    val termsAccepted: Boolean
) {

    companion object {
        val EMPTY = User(Profile.EMPTY, false)
    }
}
