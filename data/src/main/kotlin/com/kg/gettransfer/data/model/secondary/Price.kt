package com.kg.gettransfer.data.model.secondary

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Price: RealmObject() {
	@Expose
	@SerializedName("base")
	var base: PriceConverted? = null
	
	@Expose
	@SerializedName("percentage_30")
	var p30: String? = null
	
	@Expose
	@SerializedName("percentage_70")
	var p70: String? = null
	
	@Expose
	@SerializedName("without_discount")
	var withoutDiscount: PriceConverted? = null
	
	@Expose
	@SerializedName("amount")
	var amount: Double = -1.0
	
	override fun toString(): String = base.toString()
}
