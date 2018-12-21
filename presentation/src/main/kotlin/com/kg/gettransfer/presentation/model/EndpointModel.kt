package com.kg.gettransfer.presentation.model

import com.kg.gettransfer.domain.model.Endpoint

class EndpointModel(val delegate: Endpoint) : CharSequence {
    val name = delegate.name
    override val length = name.length

    override fun toString(): String = name
    override operator fun get(index: Int): Char = name.get(index)
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = name.subSequence(startIndex, endIndex)
}
