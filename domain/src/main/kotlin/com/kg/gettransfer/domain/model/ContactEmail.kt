package com.kg.gettransfer.domain.model

data class ContactEmail(val id: EmailId, val email: String) {

    enum class EmailId {
       FINANCE, INFO, PARTNER, UNKNOWN;

        override fun toString(): String = name.toLowerCase()
    }

    companion object {
        val DEFAULT_LIST = arrayListOf(
            ContactEmail(EmailId.FINANCE, "finance@gettransfer.com"),
            ContactEmail(EmailId.INFO, "info@gettransfer.com"),
            ContactEmail(EmailId.PARTNER, "partner@gettransfer.com")
        )
    }
}