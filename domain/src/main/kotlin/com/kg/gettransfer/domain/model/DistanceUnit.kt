package com.kg.gettransfer.domain.model

import kotlin.math.roundToInt

/**
 * Kilometers & miles
 */
enum class DistanceUnit {
    KM, MI;

    companion object {
        val DEFAULT_LIST = arrayListOf(KM, MI)
        private const val MI_IN_KM = 0.6214

        fun kmToMi(km: Int) = (km * MI_IN_KM).roundToInt()
    }
}
