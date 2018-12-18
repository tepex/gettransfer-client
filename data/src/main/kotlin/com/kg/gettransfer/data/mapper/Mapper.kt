package com.kg.gettransfer.data.mapper

import org.koin.standalone.KoinComponent

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers.
 *
 * @param <E> the entity type
 * @param <M> the model type
 */
interface Mapper<E, M> : KoinComponent {
    companion object {
        const val ISO_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss"
    }

    fun fromEntity(type: E): M
    fun toEntity(type: M): E
}
