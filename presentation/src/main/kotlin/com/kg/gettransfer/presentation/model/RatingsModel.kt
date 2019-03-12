package com.kg.gettransfer.presentation.model

data class RatingsModel(
    val average: Float?,
    val vehicle: Float?,
    val driver:  Float?,
    val fair:    Float?
){
    companion object {
        val BOOK_NOW_RATING = RatingsModel(4.5f, 0.0f, 0.0f, 0.0f)
    }
}
