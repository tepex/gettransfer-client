package com.kg.gettransfer.data

import com.kg.gettransfer.models.Location


/**
 * Created by denisvakulenko on 09/02/2018.
 */

class LocationDetailed(
        val title: String,
        val subtitle: String?,
        val placeID: String?,
        val la: Double?,
        val lo: Double?) {

    constructor(title: String) : this(title, null, null, null, null)

    constructor(title: String, subtitle: String, placeID: String)
            : this(title, subtitle, placeID, null, null)

    public val valid: Boolean
        get() = title.isNotEmpty() //&& la != null && lo != null

    public fun toLocation(): Location = Location(title, la!!, lo!!)
}