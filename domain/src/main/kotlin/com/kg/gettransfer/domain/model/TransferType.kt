package com.kg.gettransfer.domain.model

class TransferType(val transferName: String,
                   var isChecked: Boolean,
                   val countPersons: Int,
                   val countBaggage: Int,
                   val priceFrom: Int)