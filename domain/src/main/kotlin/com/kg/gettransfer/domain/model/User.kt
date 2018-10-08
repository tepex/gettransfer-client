package com.kg.gettransfer.domain.model

data class User(var name: String?,
                var email: String?,
                var phone: String?,
                var termsAccepted: Boolean = true)
