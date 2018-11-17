package com.kg.gettransfer.cache.mapper

import org.koin.standalone.KoinComponent

/**
 * Interface for `Entities` mappers. It provides helper methods that facilitate
 * retrieving of `Entities` from outer `room` source layers.
 *
 * @param <C> the `cached` model type
 * @param <E> the `entity` model type
 */

interface EntityMapper<C, E>: KoinComponent {
    fun fromCached(type: C): E
    fun toCached(type: E): C
}
