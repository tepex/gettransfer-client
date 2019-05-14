package com.kg.gettransfer.data

import org.koin.standalone.KoinComponent

interface PushTokenRemote : KoinComponent {
    suspend fun registerPushToken(provider: String, token: String)
    suspend fun unregisterPushToken(token: String)
}