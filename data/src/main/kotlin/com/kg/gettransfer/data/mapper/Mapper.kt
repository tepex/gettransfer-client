package com.kg.gettransfer.data.mapper

import java.text.SimpleDateFormat

import java.util.Locale

import org.slf4j.LoggerFactory

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers
 *
 * @param <T> the cached model input type
 * @param <T> the remote model input type
 * @param <V> the model return type
 */
interface Mapper<E, M> {
    companion object {
        val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val SERVER_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)
        val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        
        val log = LoggerFactory.getLogger("GTR-data")
    }
    
    fun fromEntity(type: E): M
    fun toEntity(type: M): E
}
