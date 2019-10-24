package com.kg.gettransfer.core.data

import com.kg.gettransfer.core.domain.Result

sealed class ResultData<out E> {
    data class Success<out E>(val data: E) : ResultData<E>()
    data class NetworkError(val exception: Throwable) : ResultData<Nothing>()
    data class ApiError(val code: Int, val message: String, val type: String?) : ResultData<Nothing>()
}

fun <E, M> ResultData<E>.recover(model: M): Result<M> = when (this) {
    is ResultData.Success      -> error("Receiver must be NetworkError or ApiError")
    is ResultData.NetworkError -> Result.Failure(model, map())
    is ResultData.ApiError     -> Result.Failure(model, map())
}

fun ResultData.NetworkError.map() = Result.Error.Network(exception)

fun ResultData.ApiError.map() = Result.Error.Api(code.map(), message, type)

fun Int.map(): Result.Error.Api.Code =
    Result.Error.Api.Code.values().find { it.value == this } ?: Result.Error.Api.Code.OTHER
