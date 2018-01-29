package com.kg.gettransfer.data


/**
 * Created by denisvakulenko on 29/01/2018.
 */


@Deprecated("Find another way")
data class DataStateModel (
        private val inProgress: Boolean = false,
        private val errorMessage: String? = null,
        private val dataModel: Any? = null
) {
    fun isLoading() = inProgress
    fun isError() = errorMessage != null
    fun isSuccess() = !isError()
    fun isEmpty() = !inProgress && errorMessage == null && !isSuccess()


}