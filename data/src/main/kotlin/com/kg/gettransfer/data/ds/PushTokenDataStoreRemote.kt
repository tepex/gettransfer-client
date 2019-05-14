package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.PushTokenRemote
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class PushTokenDataStoreRemote : KoinComponent {
    private val remote: PushTokenRemote by inject()

    suspend fun registerPushToken(provider: String, token: String) = remote.registerPushToken(provider, token)
    suspend fun unregisterPushToken(token: String) = remote.unregisterPushToken(token)
}