package com.kg.gettransfer.data.model

import android.content.Context

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import com.kg.gettransfer.data.model.secondary.Language
import com.kg.gettransfer.data.model.secondary.Rating

import io.realm.RealmList
import io.realm.RealmObject

import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Carrier: RealmObject() {
	@Expose
	@SerializedName("id")
	@PrimaryKey
	var id: Int = -1
	
	@Expose
	@SerializedName("title")
	var title: String? = null
	
	@Expose
	@SerializedName("approved")
	var approved: Boolean? = null
    
	@Expose
	@SerializedName("completed_transfers")
	var completedTransfers: Int = 0
	
	@Expose
	@SerializedName("languages")
	var languages: RealmList<Language> = RealmList()
	
	@Expose
	@SerializedName("ratings")
	var rating: Rating? = null
	
	@Expose
	@SerializedName("email")
	var email: String? = null
	
	@Expose
	@SerializedName("phone")
	var phone: String? = null
	
	@Expose
	@SerializedName("alternate_phone")
	var alternatePhone: String? = null

	/* Используется R! Убрать в presentation
	fun title(c: Context) = title ?: c.getString(R.string.carrier_number)+id
	*/
}