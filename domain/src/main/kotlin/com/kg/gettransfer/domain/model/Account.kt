package com.kg.gettransfer.domain.model

data class Account(var email: String,
                   var phone: String,
                   var locale: String,
                   var currency: String,
                   var distanceUnit: String,
                   var fullName: String,
                   var groups: List<String>,
                   var termsAccepted: Boolean)