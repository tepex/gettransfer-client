package com.kg.gettransfer.models

import io.realm.RealmObject
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@RealmClass
open class PassengerProfile(
        var email: String = "",
        var phone: String = "") : RealmObject() {

    fun toMap(): Map<String, Map<String, String>> {
        return mapOf("account" to mapOf("email" to email, "phone" to phone))
    }
}

