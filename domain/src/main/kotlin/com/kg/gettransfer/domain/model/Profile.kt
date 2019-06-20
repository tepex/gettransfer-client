package com.kg.gettransfer.domain.model

data class Profile(
    var fullName: String?,
    var email: String?,
    var phone: String?
) {

    companion object {
        val EMPTY = Profile(null, null, null)
    }
}
