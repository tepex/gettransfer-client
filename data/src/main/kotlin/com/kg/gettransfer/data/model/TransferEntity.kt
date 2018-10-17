package com.kg.gettransfer.data.model

open class TransferEntity(val id: Long,
                          val createdAt: String,
                          val duration: Int?,
                          val distance: Int?,
                          val status: String,
                          val from: CityPointEntity,
                          val to: CityPointEntity?,
                          val dateToLocal: String,
                          val dateReturnLocal: String?,
                          val dateRefund: String?,
                          
                       /* Имя на табличке, которую держит встречающий (сейчас поле full_name) */
                          val nameSign: String?,
                          val comment: String?,
                          val malinaCard: String?,
                          val flightNumber: String?,
                          val flightNumberReturn: String?,
                          val pax: Int,
                          val childSeats: Int,
                          val offersCount: Int,
                          val relevantCarriersCount: Int,
                          val offersUpdatedAt: String?,
                          
                          val time: Int,
                          val paidSum: MoneyEntity,
                          val remainsToPay: MoneyEntity,
                          val paidPercentage: Int,
                          val pendingPaymentId: Int?,
                          val bookNow: Boolean,
                          val bookNowExpiration: String?,
                          val transportTypeIds: List<String>,
                          val passengerOfferedPrice: String?,
                          val price: MoneyEntity?,
                          
                          val editableFields: List<String>)
