package com.kg.gettransfer.realm


import android.content.Context
import android.text.format.DateUtils
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import java.text.DateFormat.*
import java.util.*


/**
 * Created by denisvakulenko on 19/02/2018.
 */


fun Realm.getTransferAsync(id: Int): RealmResults<Transfer> =
        where(Transfer::class.java).equalTo("id", id).findAllAsync()


fun Realm.getTransfer(id: Int): Transfer? =
        where(Transfer::class.java).equalTo("id", id).findFirst()


fun RealmObject.copyToRealmOrUpdate() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        it.copyToRealmOrUpdate(this)
    }
    realm.close()
}


object Utils {
    fun dateToString(date: Date?): String {
        if (date == null) return "-"
        return getTimeInstance(SHORT).format(date) +
                "   " +
                getDateInstance().format(date)
//        return DateUtils.formatDateTime(
//                c,
//                date.time,
//                DateUtils.FORMAT_SHOW_TIME).toString() +
//                "   " +
//                DateUtils.formatDateTime(
//                        c,
//                        date.time,
//                        DateUtils.FORMAT_SHOW_DATE
//                                or DateUtils.FORMAT_ABBREV_MONTH
//                                or DateUtils.FORMAT_SHOW_YEAR)
//                        .toString()
    }

    fun dateToShortString(c: Context, date: Date?): String {
        if (date == null) return "-"
        if (date.year == Date().year) return DateUtils.formatDateTime(
                c,
                date.time,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH)
                .toString()
        return getDateInstance().format(date)
    }

    fun hoursToString(h: Int): String {
        return when {
            h >= 48 -> "${h / 24} days"
            h >= 24 -> "One day"
            else -> "$h hours"
        }
    }
}