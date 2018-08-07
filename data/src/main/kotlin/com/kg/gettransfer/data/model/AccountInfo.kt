package com.kg.gettransfer.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.Date

@RealmClass
open class AccountInfo(
        @Expose
        @SerializedName("email")
        @PrimaryKey
        var email: String? = null,

        @Expose
        @SerializedName("phone")
        var phone: String? = null,

        @Expose
        @SerializedName("locale")
        var locale: String? = null,

        @Expose
        @SerializedName("currency")
        var currency: String = "USD",

        @Expose
        @SerializedName("distance_unit")
        var distanceUnit: String = "km",

        @Expose(serialize = false, deserialize = false)
        var dateUpdated: Date? = null): RealmObject() {
    
	/* @WTF */
	//fun getDistanceUnitId(): Int = if(distanceUnit == "km") R.string.km else R.string.mi
}
