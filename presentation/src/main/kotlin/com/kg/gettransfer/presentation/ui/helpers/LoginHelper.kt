package com.kg.gettransfer.presentation.ui.helpers

import com.kg.gettransfer.extensions.firstSign
import com.kg.gettransfer.presentation.ui.Utils

//TODO mb we can add this in extensions?
object LoginHelper {

    const val CREDENTIALS_VALID = -1
    const val INVALID_EMAIL = 1
    const val INVALID_PHONE = 2

    fun formatPhone(phone: String) =
        phone.let {
            when {
                it.firstSign() == "8" -> "+7${it.substring(1)}"
                it.firstSign() == "7" -> "+$it"
                else -> it
            }
        }

    fun validateInput(input: String, isPhone: Boolean): Int {
        if (!isPhone) {
            if (!Utils.checkEmail(input)) return INVALID_EMAIL
        } else {
            if (!Utils.checkPhone(formatPhone(input))) return INVALID_PHONE
        }
        return CREDENTIALS_VALID
    }

    fun phoneIsValid(phone: String): Boolean {
        return Utils.checkPhone(formatPhone(phone))
    }

    fun emailIsValid(email: String): Boolean {
        return Utils.checkEmail(email)
    }

    fun checkIsNumber(source: String?): Boolean {
        if (source?.firstSign() == "+") return true
        return try {
            source?.toLong()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}