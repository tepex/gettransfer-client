package com.kg.gettransfer.realm


import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.R
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

    val idValid: Boolean get() = id >= 0

    var updated: Date? = null

    val justUpdated: Boolean get() = (updated?.time ?: 0) + 1000L * 10L > Date().time

    var isActive: Boolean = false

    fun update() {
        isActive = status == "new" || status == "draft" || status == "resolved"
        updated = Date()
    }

    @Expose
    @SerializedName("from")
    var from: Location? = null
    @Expose
    @SerializedName("to")
    var to: Location? = null

    @Expose
    @SerializedName("duration")
    var hireDuration: Int? = null

    fun hireDurationString(c: Context): String? {
        val d = hireDuration
        return if (d != null) hoursToString(c, d) else null
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
    var dateRefund: Date? = null

    // --

    @Expose
    @SerializedName("pax")
    var pax: Int = 1

    @Expose
    @SerializedName("name_sign")
    var nameSign: String? = null

    @Expose
    @SerializedName("transport_type_ids")
    @Required
    var transportTypes: RealmList<String?>? = null

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
    var offersChangedDate: Date? = null

    var offersTriedToUpdateDate: Long = 0
    var offersUpdatedDate: Long = 0

    val needAndCanUpdateOffers: Boolean
        get() {
            if (offersTriedToUpdateDate > System.currentTimeMillis() - 1000) return false
            return offersUpdatedDate < offersChangedDate?.time ?: 1 || offers.size != offersCount
        }

    fun populateFromOldTransfer(oldTransfer: Transfer?) {
        oldTransfer ?: return
        offersUpdatedDate = oldTransfer.offersUpdatedDate
        offers = oldTransfer.offers
    }

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

    fun isActiveNew() = status == "new" && !bookNow || status == "draft" && bookNow
    fun isActiveConfirmed() = status == "new" && bookNow || status == "performed" && !bookNow

    fun strStatus(c: Context) = when {
        status == "new" && !bookNow || status == "draft" && bookNow ->
            c.getString(R.string.status_active)
        status == "new" && bookNow || status == "performed" && !bookNow ->
            c.getString(R.string.status_confirmed)
        status == "outdated" ->
            c.getString(R.string.status_expired)
        status == "canceled" ->
            c.getString(R.string.status_canceled)
        status == "rejected" ->
            c.getString(R.string.status_rejected)
        else ->
            c.getString(R.string.status_archive)
    }
}
