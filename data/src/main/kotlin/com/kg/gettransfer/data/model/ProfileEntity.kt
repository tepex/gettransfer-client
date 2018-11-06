package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileEntity(val name: String? = null, val email: String? = null, val phone: String? = null)
