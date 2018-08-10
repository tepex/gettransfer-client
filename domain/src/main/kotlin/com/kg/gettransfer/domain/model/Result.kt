package com.kg.gettransfer.domain.model

data class Result<out T>(val success: T? = null, val error: Throwable? = null)
