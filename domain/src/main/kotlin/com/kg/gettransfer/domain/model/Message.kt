package com.kg.gettransfer.domain.model

import java.util.Date

data class Message(
        override val id: Long,
        var accountId: Long,
        val transferId: Long,
        val createdAt: Date,
        var readAt: Date?,
        val text: String,
        val sendAt: Long? = null
) : Entity()