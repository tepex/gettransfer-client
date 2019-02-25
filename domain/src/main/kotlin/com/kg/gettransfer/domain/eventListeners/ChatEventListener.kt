package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.Message

interface ChatEventListener {
    fun onNewMessageEvent(message: Message)
    fun onMessageReadEvent(message: Message)
}