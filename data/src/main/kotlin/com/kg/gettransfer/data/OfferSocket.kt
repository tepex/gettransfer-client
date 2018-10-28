package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.OfferEntityListener

interface OfferSocket {
    fun setListener(listener: OfferEntityListener)
    fun removeListener(listener: OfferEntityListener)
}
