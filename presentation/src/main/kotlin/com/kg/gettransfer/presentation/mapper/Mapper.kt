package com.kg.gettransfer.presentation.mapper

import android.location.Location

import com.kg.gettransfer.domain.model.Point

import org.koin.core.KoinComponent

/**
 * Interface for ui mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers.
 */
interface Mapper<V, M>: KoinComponent {
    fun fromView(type: V): M
    fun toView(type: M): V

    companion object {
        private fun point2Location(point: Point) = Location("").apply {
            latitude = point.latitude
            longitude = point.longitude
        }

        fun checkDistance(from: Point, to: Point?) = to?.let { (point2Location(from).distanceTo(point2Location(it)) / 1000).toInt() }
    }
}
