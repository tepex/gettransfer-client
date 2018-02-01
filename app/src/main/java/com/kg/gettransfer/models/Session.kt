package com.kg.gettransfer.models


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 01/02/2018.
 */


@RealmClass
open class Session : RealmObject() {
    @PrimaryKey
    var email : String? = null

    var phone : String? = null

    var accessToken : String? = null
}