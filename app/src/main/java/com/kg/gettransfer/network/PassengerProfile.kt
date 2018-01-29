package com.kg.gettransfer.network


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class PassengerProfile(
        val email: String,
        val phone: String) {

    fun toMap(): Map<String, Map<String, String>> {
        return mapOf("account" to mapOf("email" to email, "point" to phone))
    }
}

