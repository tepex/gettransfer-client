package com.kg.gettransfer.domain.model

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

data class Result<M>(
    val model: M,
    val error: ApiException? = null,
    val cacheError: DatabaseException? = null
) {

    fun isError() = error == null
    fun isNotError() = if (error == null) model else null    //get data if no need to handle error
}
