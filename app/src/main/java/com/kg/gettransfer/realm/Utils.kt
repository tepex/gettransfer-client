package com.kg.gettransfer.realm


import android.content.Context
import android.text.format.DateUtils
import com.kg.gettransfer.R
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


fun <T : RealmObject> T.saveAndGetUnmanaged(): T {
    var o = this
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        o = it.copyToRealmOrUpdate(this)
    }
    val copy = realm.copyFromRealm(o)
    realm.close()
    return copy
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

    fun hoursToString(c: Context, h: Int): String {
        return when {
            h >= 24 -> "${h / 24} " + c.getPlString(R.string.pl_days).forN(h / 24)
            else -> "$h " + c.getPlString(R.string.pl_hours).forN(h)
        }
    }
}


public interface PlString {
    fun forN(n: Int): String
}

public class PlStringSimple(
        private val s1: String,
        private val sN: String)
    : PlString {

    override fun forN(n: Int): String {
        if (n == 1) return s1
        return sN
    }
}

public class PlStringRus : PlString {
    private val s1: String
    private val s24: String
    private val s059: String

    constructor(split: List<String>) {
        s1 = split[0]
        s24 = split[1]
        s059 = split[2]
    }

    override fun forN(n: Int): String {
        if (n % 100 in 10..20) return s059
        if (n % 10 in 2..3) return s24
        if (n % 10 == 1) return s1
        return s059
    }
}

public fun Context.getPlString(id: Int): PlString {
    val s = this.getString(id)
    val split = s.split("|")
    if (split.size == 3) {
        return PlStringRus(split)
    } else {
        return PlStringSimple(split[0], split[1])
    }
}