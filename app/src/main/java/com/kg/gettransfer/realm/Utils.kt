package com.kg.gettransfer.realm


import android.content.Context
import android.text.format.DateUtils
import io.realm.Realm
import io.realm.RealmResults
import java.util.*


/**
 * Created by denisvakulenko on 19/02/2018.
 */


fun Realm.getTransferAsync(id: Int): RealmResults<Transfer> =
        where(Transfer::class.java).equalTo("id", id).findAllAsync()


fun Realm.getTransfer(id: Int): Transfer? =
        where(Transfer::class.java).equalTo("id", id).findFirst()


object Utils {
    fun dateToString(c: Context, date: Date?): String {
        if (date == null) return "-"
        return DateUtils.formatDateTime(
                c,
                date.time,
                DateUtils.FORMAT_SHOW_TIME).toString() +
                "   " +
                DateUtils.formatDateTime(
                        c,
                        date.time,
                        DateUtils.FORMAT_SHOW_DATE
                                or DateUtils.FORMAT_ABBREV_MONTH
                                or DateUtils.FORMAT_SHOW_YEAR)
                        .toString()
    }

    fun hoursToString(h: Int): String {
        return when {
            h >= 48 -> "${h / 24} days"
            h >= 24 -> "One day"
            else -> "$h hours"
        }
    }
}