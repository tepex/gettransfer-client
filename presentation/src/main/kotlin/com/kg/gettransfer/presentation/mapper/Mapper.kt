package com.kg.gettransfer.presentation.mapper

import org.koin.core.KoinComponent

/**
 * Interface for ui mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers.
 */
interface Mapper<V, M>: KoinComponent {
    fun fromView(type: V): M
    fun toView(type: M): V
}
