package com.kg.gettransfer.module.http.json


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class PassengerProfile(
        var email: String = "",
        var phone: String = "") {

    fun toMap(): Map<String, Map<String, String>> {
        return mapOf("account" to mapOf("email" to email, "phone" to phone))
    }
}

