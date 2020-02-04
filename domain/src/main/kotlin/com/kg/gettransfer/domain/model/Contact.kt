package com.kg.gettransfer.domain.model

sealed class Contact<String> {
    class EmailContact(val email: String) : Contact<String>()
    class PhoneContact(val phone: String) : Contact<String>()
}
