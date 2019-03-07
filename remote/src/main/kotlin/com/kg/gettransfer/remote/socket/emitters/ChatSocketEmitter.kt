package com.kg.gettransfer.remote.socket.emitters

import com.kg.gettransfer.data.socket.ChatEventEmitter

interface ChatSocketEmitter : ChatEventEmitter {
    companion object {
        const val JOIN_ROOM_EVENTS = "chat.join-room"
        const val LEAVE_ROOM_EVENTS = "chat.leave-room"
        const val SEND_MESSAGE_EVENTS = "chat.send-message"
        const val READ_MESSAGE_EVENTS = "chat.read-message"
    }
}