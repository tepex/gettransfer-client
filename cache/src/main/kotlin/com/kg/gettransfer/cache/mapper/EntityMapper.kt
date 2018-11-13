package com.kg.gettransfer.cache.mapper

/**
 * Interface for `Entities` mappers. It provides helper methods that facilitate
 * retrieving of `Entities` from outer `room` source layers.
 *
 * @param <C> the `cached` model type
 * @param <E> the `entity` model type
 */

interface EntityMapper<C, E> {
    fun fromCached(type: C): E
    fun toCached(type: E): C
}
