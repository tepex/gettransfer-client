package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MoneyEntity(val default: String, val preferred: String?)
