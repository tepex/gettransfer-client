package com.kg.gettransfer.domain.model

data class Profile(
    var fullName: String?,
    var email: String?,
    var phone: String?
) {
    fun hasData() =
            !email.isNullOrEmpty()
                    && !phone.isNullOrEmpty()

    fun clear() {
        email = null
        phone = null
    }
}
