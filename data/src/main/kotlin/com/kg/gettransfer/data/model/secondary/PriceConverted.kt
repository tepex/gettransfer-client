package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class PriceConverted(
        @Expose
        @SerializedName("default")
        var defaultCurrency: String? = null,

        @Expose
        @SerializedName("preferred")
        var preferredCurrency: String? = null): RealmObject() {

	override fun toString(): String =
		if(preferredCurrency == null) defaultCurrency ?: ""
		else "($defaultCurrency) $preferredCurrency"
		
	fun toStringMultiline(): String =
		if(preferredCurrency == null) defaultCurrency ?: ""
		else "$defaultCurrency\n$preferredCurrency"
}
