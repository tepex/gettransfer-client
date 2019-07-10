package com.kg.gettransfer.extensions

import java.math.RoundingMode

fun Double.toHalfEvenRoundedFloat() = toBigDecimal().setScale(1, RoundingMode.HALF_EVEN).toFloat()