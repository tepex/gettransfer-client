package com.kg.gettransfer.realm.secondary


import io.realm.RealmObject
import io.realm.annotations.RealmClass
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by denisvakulenko on 07/03/2018.
 */


@RealmClass
open class ZonedDate(
        var date: Date = Date(0),
        var zoneString: String? = null)
    : RealmObject() {

//    companion object {
//        fun fromString(s: String) : ZonedDate {
//
//            return
//        }
//    }

    fun getTimeZone() = TimeZone.getTimeZone(zoneString)

    override fun toString(): String {
        val sdtf = SimpleDateFormat("hh:mm dd MMM yyyy")
        sdtf.timeZone = getTimeZone()
        return sdtf.format(date)
    }
}