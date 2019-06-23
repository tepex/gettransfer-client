package com.kg.gettransfer.domain

class DatabaseException(val code: Int, val details: String) : RuntimeException(details) {
    companion object {
        const val ILLEGAL_STATE = 0
    }

    fun isIllegalStateException() = code == ILLEGAL_STATE
}
