package com.kg.gettransfer.network


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class Location(
        val name: String,
        val point: String) {

    constructor(name: String, la: Int, lo: Int) : this(name, "(" + la.toString() + "," + lo.toString() + ")")

    fun toMap(): Map<String, String> {
        return mapOf("name" to name, "point" to point)
    }
}