package com.kg.gettransfer.data.mapper

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Interface for model mappers. It provides helper methods that facilitate
 * retrieving of models from outer data source layers
 *
 * @param <T> the cached model input type
 * @param <T> the remote model input type
 * @param <V> the model return type
 */
interface Mapper<E, D> {
    companion object {
        val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val SERVER_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.US)
        val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    }
    
    fun fromEntity(type: E): D
    fun toEntity(type: D): E
}
