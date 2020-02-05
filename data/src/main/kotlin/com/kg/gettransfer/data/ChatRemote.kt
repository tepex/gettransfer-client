package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ChatEntity
import org.koin.core.KoinComponent

interface ChatRemote : KoinComponent {
    suspend fun getChat(transferId: Long): ChatEntity
}
