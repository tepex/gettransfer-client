package com.kg.gettransfer.domain.model

data class ChatBadgeEvent(
        val transferId: Long,
        val clearBadge: Boolean
)