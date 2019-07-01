package com.kg.gettransfer.remote

import com.kg.gettransfer.data.PushTokenRemote
import org.koin.core.get

class PushTokenRemoteImpl : PushTokenRemote {

    private val core = get<ApiCore>()

    override suspend fun registerPushToken(provider: String, token: String) {
        core.tryTwice { core.api.registerPushToken(provider, token) }
    }

    override suspend fun unregisterPushToken(token: String) {
        core.tryTwice { core.api.unregisterPushToken(token) }
    }
}
