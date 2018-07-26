package com.kg.gettransfer.data

import android.content.Context
import android.text.format.DateUtils

import com.kg.gettransfer.data.model.Transfer
import com.kg.gettransfer.data.model.secondary.ZonedDate

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults

import java.util.Date

fun Realm.getTransferAsync(id: Int): RealmResults<Transfer> =
	where(Transfer::class.java).equalTo("id", id).findAllAsync()

fun Realm.getTransfer(id: Int): Transfer? =
	where(Transfer::class.java).equalTo("id", id).findFirst()

fun <T: RealmObject> T.saveAndGetUnmanaged(): T {
	var o = this
	val realm = Realm.getDefaultInstance()
	realm.executeTransaction { o = it.copyToRealmOrUpdate(this) }
	val copy = realm.copyFromRealm(o)
	realm.close()
	return copy
}


/*  It's a SHAME!
object Utils {
	fun dateToString(date: Date?): String {
		if(date == null) return "-"
		return "${getTimeInstance(SHORT).format(date)}   ${getDateInstance().format(date)}"
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
	
	fun dateToShortString(c: Context, zd: ZonedDate?): String {
		if(zd == null) return "-"
			
		val date = zd.date
		//TODO J!NJKNKJFNAD
		// Пробито очередное дно!
		if(date.year == Date().year) return DateUtils.formatDateTime(
                c,
                date.time,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH)
                .toString()
		return getDateInstance().format(date)
	}
	
	fun hoursToString(c: Context, h: Int): String {
		return when {
			h >= 24 -> "${h / 24} ${c.getPlString(R.string.pl_days).forN(h / 24)}"
			else -> "$h ${c.getPlString(R.string.pl_hours).forN(h)}"
		}
	}
	
	fun kmToString(c: Context, km: Double, unit: Int = R.string.km): String {
		if(unit == R.string.mi) {
			val miles = (km * 1.609344).toInt()
			return miles.toString() + " " + c.getPlString(R.string.pl_mi).forN(miles)
		}
		return km.toInt().toString() + " " + c.getString(R.string.km)
	}
}
*/

public interface PlString {
	fun forN(n: Int): String
}

public class PlStringSimple(private val s1: String, private val sN: String): PlString {
	constructor(s: String): this(s, s)
	
	override fun forN(n: Int): String {
		if(n == 1) return s1
		return sN
	}
}

public class PlStringRus: PlString {
	private val s1: String
	private val s24: String
	private val s059: String
	
	constructor(split: List<String>) {
		s1 = split[0]
		s24 = split[1]
		s059 = split[2]
	}
	
	override fun forN(n: Int): String {
		if(n % 100 in 10..20) return s059
		if(n % 10 in 2..4) return s24
		if(n % 10 == 1) return s1
		return s059
	}
}

public fun Context.getPlString(id: Int): PlString {
	val s = this.getString(id)
	val split = s.split("|")
	if(split.size == 3) return PlStringRus(split)
	else return if(split.size == 1) PlStringSimple(split[0]) else PlStringSimple(split[0], split[1])
}
