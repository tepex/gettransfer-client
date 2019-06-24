package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kg.gettransfer.data.model.BookNowOfferEntity

data class BookNowOfferModel(
    @SerializedName(BookNowOfferEntity.AMOUNT) @Expose val amount: Double,
    @SerializedName(BookNowOfferEntity.BASE) @Expose val base: MoneyModel,
    @SerializedName(BookNowOfferEntity.WITHOUT_DISCOUNT) @Expose val withoutDiscount: MoneyModel?
)

fun Map.Entry<String, BookNowOfferModel>.map() =
    BookNowOfferEntity(value.amount, value.base.map(), value.withoutDiscount?.map())
