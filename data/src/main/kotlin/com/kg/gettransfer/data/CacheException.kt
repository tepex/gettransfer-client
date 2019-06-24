package com.kg.gettransfer.data

import com.kg.gettransfer.domain.DatabaseException

class CacheException(
    val code: Int,
    val details: String
) : RuntimeException(details) {

    fun map() = DatabaseException(code, details)

    companion object {
        const val ILLEGAL_STATE = DatabaseException.ILLEGAL_STATE
    }
}
