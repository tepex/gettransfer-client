package com.kg.gettransfer.presentation

import android.util.Log

class MarkerAnimationHelper(private val startPositionY: Int, val defaultElevationPx: Float) {

    companion object {
        const val DEFAULT_ELEVATION = 5F
    }

    fun getElevationForAnimate(currentPositionY: Int): Float {
        val elev = (currentPositionY - startPositionY).toFloat()
        Log.i("FindElevation", "getElev " + elev)
        return elev
    }
}