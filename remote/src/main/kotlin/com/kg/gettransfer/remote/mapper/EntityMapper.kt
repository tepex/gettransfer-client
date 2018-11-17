package com.kg.gettransfer.remote.mapper

import org.koin.standalone.KoinComponent

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers
 *
 * @param <M> the remote model input type
 * @param <E> the entity model output type
 */
interface EntityMapper<M, E>: KoinComponent {
    fun fromRemote(type: M): E
    fun toRemote(type: E): M
}