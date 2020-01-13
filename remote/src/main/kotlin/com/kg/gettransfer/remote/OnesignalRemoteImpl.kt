package com.kg.gettransfer.remote

import com.kg.gettransfer.data.OnesignalRemote
import com.kg.gettransfer.remote.model.PlayerIdModel

import org.koin.core.get

class OnesignalRemoteImpl : OnesignalRemote {
    private val core = get<ApiCore>()

    override suspend fun associatePlayerId(playerId: String) {
        core.tryTwice { core.api.associatePlayerId(PlayerIdModel(playerId)) }
    }
}
