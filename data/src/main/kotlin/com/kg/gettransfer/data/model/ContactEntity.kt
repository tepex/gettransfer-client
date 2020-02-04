package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Contact

sealed class ContactEntity<String>

class EmailContactEntity<String>(val email: String) : ContactEntity<String>()

class PhoneContactEntity<String>(val phone: String) : ContactEntity<String>()

fun Contact<String>.map(): ContactEntity<String> = when (this) {
    is Contact.EmailContact -> EmailContactEntity(email)
    is Contact.PhoneContact -> PhoneContactEntity(phone)
}
