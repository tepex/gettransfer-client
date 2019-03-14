package com.kg.gettransfer.extensions

import com.willy.ratingbar.ScaleRatingBar

inline var ScaleRatingBar.visibleRating: Float
    get() = rating
    set(value) {
        rating = value
        isVisible = value != NO_RATE
    }

const val NO_RATE = 0f