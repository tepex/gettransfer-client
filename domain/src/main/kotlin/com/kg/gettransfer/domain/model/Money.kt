package com.kg.gettransfer.domain.model

import java.io.Serializable

data class Money(
    val def: String,
    val preferred: String?
): Serializable
