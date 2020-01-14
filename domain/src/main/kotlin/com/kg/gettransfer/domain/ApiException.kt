@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.domain

class ApiException(
    val code: Int,
    val details: String,
    val isHttpException: Boolean,
    val type: String? = null
) : RuntimeException(details) {

    fun isNoUser() = code == NO_USER

    fun isNotLoggedIn() = code == NOT_LOGGED_IN

    fun isNotFound() = code == NOT_FOUND

    fun isPhoneTaken() = type == TYPE_PHONE_TAKEN

    fun isEmailTaken() = type == TYPE_EMAIL_TAKEN

    fun isTooManyRequests() = code == TOO_MANY_REQUESTS

    fun isAccountExistError() = type == TYPE_ACCOUNT_EXIST

    fun isEmailNotChangeableError() = checkDetailsText(DETAILS_EMAIL_NOT_CHANGEABLE)

    fun isPhoneNotChangeableError() = checkDetailsText(DETAILS_PHONE_NOT_CHANGEABLE)

    fun isNewEmailAlreadyTakenError() = checkDetailsText(DETAILS_NEW_EMAIL_TAKEN)

    fun isNewPhoneAlreadyTakenError() = checkDetailsText(DETAILS_NEW_PHONE_TAKEN)

    fun isNewEmailInvalid() = checkDetailsText(DETAILS_NEW_EMAIL_INVALID)

    fun isNewPhoneInvalid() = checkDetailsText(DETAILS_NEW_PHONE_INVALID)

    fun checkExistedAccountField() = when {
        checkDetailsText(DETAILS_REDIRECT_PHONE) -> PHONE_EXISTED
        checkDetailsText(DETAILS_REDIRECT_EMAIL) -> EMAIL_EXISTED
        else -> PHONE_EXISTED
    }

    private fun checkDetailsText(str: String) = details.indexOf(str) >= 0

    fun isEarlyDateError() = details == DETAILS_DATE_EARLY

    fun isLateDateError() = details == DETAILS_DATE_LATE

    /* PAYMENT ERRORS */
    fun isBigPriceError() = code == UNPROCESSABLE && details == DETAILS_BIG_PRICE

    fun isOfferUnavailableError() = code == UNPROCESSABLE && details == DETAILS_OFFER_UNAVAILABLE

    fun isTransferStatusMismatchError() = details.contains(DETAILS_TRANSFER_STATUS_MISMATCH)

    fun isPasswordError() = code == UNPROCESSABLE && details.startsWith(PASSWORD_ERROR)

    companion object {
        const val APP_ERROR         = 0
        const val NETWORK_ERROR     = -1
        const val GEOCODER_ERROR    = -2
        const val NO_USER           = 400
        const val NOT_LOGGED_IN     = 403
        const val NOT_FOUND         = 404
        const val UNPROCESSABLE     = 422
        const val TOO_MANY_REQUESTS = 429

        const val INTERNAL_SERVER_ERROR = 500
        const val CONNECTION_TIMED_OUT  = 522

        const val DETAILS_BIG_PRICE = "{price=[is_too_big]}"
        const val DETAILS_OFFER_UNAVAILABLE = "{offer_id=[blocked]}"
        const val DETAILS_DATE_EARLY = "{date=[is too early]}"
        const val DETAILS_DATE_LATE = "{date=[is too late]}"
        const val DETAILS_TRANSFER_STATUS_MISMATCH = "transfer_status_mismatch"

        const val TYPE_ACCOUNT_EXIST = "account_exists"
        const val TYPE_PHONE_TAKEN = "phone_taken"
        const val TYPE_EMAIL_TAKEN = "email_taken"
        const val TYPE_EMAIL_INVALID = "email_invalid"
        const val TYPE_PHONE_INVALID = "phone_invalid"
        const val TYPE_PHONE_UNPROCESSABLE = "unprocessable"

        const val DETAILS_NEW_EMAIL_INVALID = "email=[invalid]"
        const val DETAILS_NEW_PHONE_INVALID = "phone=[invalid]"
        const val DETAILS_NEW_EMAIL_TAKEN = "email=[already_taken]"
        const val DETAILS_NEW_PHONE_TAKEN = "phone=[already_taken]"
        const val DETAILS_EMAIL_NOT_CHANGEABLE = "account=[email_not_manually_changeable]"
        const val DETAILS_PHONE_NOT_CHANGEABLE = "account=[phone_not_manually_changeable]"
        const val DETAILS_REDIRECT_EMAIL = "email"
        const val DETAILS_REDIRECT_PHONE = "phone"

        const val EMAIL_EXISTED = "email_existed"
        const val PHONE_EXISTED = "phone_existed"

        const val PASSWORD_ERROR = "{password="
    }
}
