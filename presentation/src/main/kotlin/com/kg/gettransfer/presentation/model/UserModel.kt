package com.kg.gettransfer.presentation.model

data class UserModel(var name: String?,
                     var email: String?,
                     var phone: String?,
                     var termsAccepted: Boolean = true)
