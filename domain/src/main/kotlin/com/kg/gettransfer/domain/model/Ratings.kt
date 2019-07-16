package com.kg.gettransfer.domain.model

data class Ratings(
    private val hiddenAverage: Double?,
    val vehicle: Double?,
    val driver: Double?,
    val communication: Double?
) {

    val average: Double
        get() = hiddenAverage ?: listOfNotNull<Double>(vehicle, driver, communication).let {
            if (it.isNotEmpty()) it.average() else NO_RATING }

    companion object {
        const val NO_RATING = 0.0
        val EMPTY = Ratings(null, null, null, null)
        val BOOK_NOW_RATING = Ratings(4.5, null, null, null)
    }
}
