package com.kg.gettransfer.realm


import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.secondary.Language
import com.kg.gettransfer.realm.secondary.Rating
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass


/**
 * Created by denisvakulenko on 20/02/2018.
 */


@RealmClass
open class Carrier : RealmObject() {
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

    fun title(c: Context) = title ?: c.getString(R.string.carrier_number)+id
}