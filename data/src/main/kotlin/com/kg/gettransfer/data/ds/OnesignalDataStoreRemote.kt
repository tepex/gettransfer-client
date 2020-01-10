package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.OnesignalRemote

import org.koin.core.KoinComponent
import org.koin.core.inject

open class OnesignalDataStoreRemote : KoinComponent {

    private val remote: OnesignalRemote by inject()

    suspend fun associatePlayerId(playerId: String) = remote.associatePlayerId(playerId)
}
