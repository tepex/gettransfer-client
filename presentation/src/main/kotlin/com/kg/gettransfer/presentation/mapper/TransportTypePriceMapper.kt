package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.presentation.model.TransportTypePriceModel

open class TransportTypePriceMapper : Mapper<TransportTypePriceModel, TransportTypePrice> {
    override fun toView(type: TransportTypePrice) =
        TransportTypePriceModel(
            minFloat = type.minFloat,
            min      = type.min,
            bookNow  = type.bookNow
        )

    override fun fromView(type: TransportTypePriceModel): TransportTypePrice { throw UnsupportedOperationException() }
}
