package com.kg.gettransfer.realm


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.realm.Utils.hoursToString
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required
import java.util.*


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@RealmClass
open class Transfer : RealmObject() {
    @Expose
    @SerializedName("id")
    @PrimaryKey
    var id: Int = -1

    @Expose
    @SerializedName("from")
    var from: Location? = null
    @Expose
    @SerializedName("to")
    var to: Location? = null

    @Expose
    @SerializedName("duration")
    var hireDuration: Int? = null

    val hireDurationString: String?
        get() {
            val d = hireDuration
            return if (d != null) hoursToString(d) else null
        }

    @Expose
    @SerializedName("distance")
    var routeDistance: Int? = null

    @Expose
    @SerializedName("time")
    var routeDuration: Int? = null

    @Expose
    @SerializedName("status")
    var status: String? = null

    var isActive: Boolean = false

    fun updateIsActive() {
        isActive = status == "new" || status == "draft" || status == "resolved"
    }

    @Expose
    @SerializedName("book_now")
    var bookNow: Boolean = false

    @Expose
    @SerializedName("date_to_local")
    var dateTo: Date? = null

    @Expose
    @SerializedName("date_return_local")
    var dateReturn: Date? = null

    // --

    @Expose
    @SerializedName("date_refund")
    var date_refund: Date? = null

    // --

    @Expose
    @SerializedName("pax")
    var pax: Int = 1

    @Expose
    @SerializedName("name_sign")
    var nameSign: String? = null

    @Expose
    @SerializedName("transport_types")
    @Required
    var transportTypes: RealmList<java.lang.Integer?>? = null

    @Expose
    @SerializedName("child_seats")
    var childSeats: String? = null

    @Expose
    @SerializedName("comment")
    var comment: String? = null

    // --

    @Expose
    @SerializedName("flight_number")
    var flightNumber: String? = null

    // --

    @Expose
    @SerializedName("offers_count")
    var offersCount: Int = 0

    @Expose
    @SerializedName("offers_updated_at")
    var offersUpdatedAt: Date? = null

    // --

    @Expose
    @SerializedName("relevant_carrier_profiles_count")
    var relevantCarrierProfilesCount: Int? = null

    // --

    @Expose
    @SerializedName("malina_card")
    var malinaCard: String? = null

    // --

    var offers: RealmList<Offer> = RealmList()

    val strStatus: String
        get() = when {
            status == "new" && !bookNow || status == "draft" && bookNow ->
                "Active"
            status == "new" && bookNow || status == "performed" && !bookNow ->
                "Confirmed"
            status == "outdated" ->
                "Expired"
            status == "canceled" ->
                "Canceled"
            status == "rejected" ->
                "Rejected"
            else ->
                "Archive"
        }
}
