package com.kg.gettransfer.data

import org.koin.core.KoinComponent

interface PushTokenRemote : KoinComponent {

    suspend fun registerPushToken(provider: String, token: String)

    suspend fun unregisterPushToken(token: String)
}
