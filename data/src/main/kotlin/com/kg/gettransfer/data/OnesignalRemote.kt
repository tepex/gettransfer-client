package com.kg.gettransfer.data

import org.koin.core.KoinComponent

interface OnesignalRemote : KoinComponent {
    suspend fun associatePlayerId(playerId: String)
}
