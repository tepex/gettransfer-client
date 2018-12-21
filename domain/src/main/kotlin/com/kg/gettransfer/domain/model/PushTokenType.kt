package com.kg.gettransfer.domain.model

enum class PushTokenType {
    FCM, APN;

    override fun toString() = name.toLowerCase()
}
