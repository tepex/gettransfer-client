package com.kg.gettransfer.data.model.secondary

import io.realm.RealmObject
import io.realm.annotations.RealmClass

/*
import java.text.SimpleDateFormat
*/
import java.util.Date
import java.util.TimeZone

@RealmClass
open class ZonedDate(var date: Date = Date(0), var zoneString: String? = null): RealmObject() {
	/*
	companion object {
		private val SDF = SimpleDateFormat("hh:mm dd MMM yyyy")
	}
	*/

	fun getTimeZone() = TimeZone.getTimeZone(zoneString)
	
	override fun toString(): String {
		//return SDF.getTimeZone().format(date)
		return "ZoneDate->qqq"
	}
}
