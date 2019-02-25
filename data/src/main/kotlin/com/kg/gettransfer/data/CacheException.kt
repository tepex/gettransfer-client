package com.kg.gettransfer.data

import com.kg.gettransfer.domain.DatabaseException
import java.lang.RuntimeException

class CacheException(val code: Int, val details: String) : RuntimeException(details) {
    companion object {
        const val ILLEGAL_STATE = DatabaseException.ILLEGAL_STATE
    }
}