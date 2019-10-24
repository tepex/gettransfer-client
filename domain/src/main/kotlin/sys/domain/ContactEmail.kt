package com.kg.gettransfer.sys.domain

data class ContactEmail(
    val id: Id,
    val email: String
) {

    enum class Id {
        FINANCE, INFO, PARTNER, UNKNOWN;

        override fun toString(): String = name.toLowerCase()
    }
}
