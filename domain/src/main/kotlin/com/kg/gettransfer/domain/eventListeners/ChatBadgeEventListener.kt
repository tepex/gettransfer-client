package com.kg.gettransfer.domain.eventListeners

import com.kg.gettransfer.domain.model.ChatBadgeEvent

interface ChatBadgeEventListener {
    fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEvent)
}
