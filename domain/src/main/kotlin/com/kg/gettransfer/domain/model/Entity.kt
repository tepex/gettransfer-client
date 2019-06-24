package com.kg.gettransfer.domain.model

import java.util.Collections

interface Entity {
    val id: Long
}

/* For API < v.24 */
fun <E : Entity> List<E>.sortDescendant() = ArrayList(this).apply {
    Collections.sort(this, object : Comparator<E> {
        override fun compare(a: E, b: E) = (b.id - a.id).toInt()
    })
}
