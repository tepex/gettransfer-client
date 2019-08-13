package com.kg.gettransfer.core.domain

sealed class Result<out M> {

    val isSuccess = this is Success
    val isFailure = !isSuccess

    fun getModel(): M = when (this) {
        is Success -> remote
        is Failure -> cached
    }

    data class Success<out M>(internal val remote: M) : Result<M>()
    data class Failure<out M>(internal val cached: M, val error: Error) : Result<M>()

    sealed class Error {
        data class Network(val exception: Throwable) : Error()
        data class Api(val code: Code, val message: String, val type: String?) : Error() {
            @Suppress("MagicNumber")
            enum class Code(val value: Int) {
                NO_USER(400), NOT_LOGGED_IN(403), NOT_FOUND(404), UNPROCESSABLE(422), OTHER(-1)
            }
        }
    }
}
