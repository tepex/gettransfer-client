package com.kg.gettransfer.presentation.model

import java.util.Date

data class MessageModel(
        val id: Long,
        val accountId: Long,
        val transferId: Long,
        val createdAt: Date,
        var readAt: Date?,
        val text: String,
        val sendAt: Long? = null
)