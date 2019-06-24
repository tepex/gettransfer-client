package com.kg.gettransfer.domain

class ApiException(
    val code: Int,
    val details: String,
    val type: String? = null
) : RuntimeException(details) {

    fun isNoUser() = code == NO_USER
    fun isNotLoggedIn() = code == NOT_LOGGED_IN
    fun isNotFound() = code == NOT_FOUND
    fun isPhoneTaken() = type == "phone_taken"

    fun isAccountExistError() = type == "account_exists"
    fun isBadCodeError() = details.indexOf("bad_code_or_email") >= 0
    fun isEmailNotChangebleError() = details.indexOf("account=[email_not_manually_changeable]") >= 0
    fun isEmailAlreadyTakenError() = details.indexOf("new_email=[already_taken]") >= 0
    fun checkExistedAccountField() = when {
        details.indexOf("phone") >= 0 -> PHONE_EXISTED
        details.indexOf("email") >= 0 -> EMAIL_EXISTED
        else -> PHONE_EXISTED
    }

    /* PAYMENT ERRORS */
    fun isBigPriceError() = code == UNPROCESSABLE && details == "{price=[is_too_big]}"

    companion object {
        const val APP_ERROR      = 0
        const val NETWORK_ERROR  = -1
        const val GEOCODER_ERROR = -2
        const val NO_USER        = 400
        const val NOT_LOGGED_IN  = 403
        const val NOT_FOUND      = 404
        const val UNPROCESSABLE  = 422

        const val INTERNAL_SERVER_ERROR = 500
        const val CONNECTION_TIMED_OUT  = 522

        const val EMAIL_EXISTED = "email_existed"
        const val PHONE_EXISTED = "phone_existed"
    }
}
