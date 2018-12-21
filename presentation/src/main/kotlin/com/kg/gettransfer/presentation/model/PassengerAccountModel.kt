package com.kg.gettransfer.presentation.model

data class PassengerAccountModel(
    val id: Long,
    val profile: ProfileModel,
    val lastSeen: String
)
