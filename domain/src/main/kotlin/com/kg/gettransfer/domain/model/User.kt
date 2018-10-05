package com.kg.gettransfer.domain.model

data class User(var email: String?,
                var phone: String?,
                var fullName: String?,
                var termsAccepted: Boolean = true)
