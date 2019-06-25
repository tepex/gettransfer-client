package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.model.MessageNewSocketModel
import com.kg.gettransfer.data.model.MessageReadSocketModel
import com.kg.gettransfer.remote.socket.emitters.ChatSocketEmitter
import com.kg.gettransfer.remote.socket.emitters.ChatSocketEmitter.Companion.JOIN_ROOM_EVENTS
import com.kg.gettransfer.remote.socket.emitters.ChatSocketEmitter.Companion.LEAVE_ROOM_EVENTS
import com.kg.gettransfer.remote.socket.emitters.ChatSocketEmitter.Companion.READ_MESSAGE_EVENTS
import com.kg.gettransfer.remote.socket.emitters.ChatSocketEmitter.Companion.SEND_MESSAGE_EVENTS
import org.json.JSONObject
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject

class ChatSocketImpl : ChatSocketEmitter, KoinComponent {

    private val eventReceiver: ChatDataStoreReceiver by inject()
    private val socketManager: SocketManager = get()

    override fun onJoinRoomEmit(transferId: Long) {
        socketManager.emitEvent(JOIN_ROOM_EVENTS, transferId)
    }

    override fun onLeaveRoomEmit(transferId: Long) {
        socketManager.emitEvent(LEAVE_ROOM_EVENTS, transferId, true)
    }

    override fun onSendMessageEmit(transferId: Long, text: String): Boolean {
        JSONObject().apply {
            put(MessageNewSocketModel.ROOM, transferId)
            put(MessageNewSocketModel.TEXT, text)
        }.let { return socketManager.emitEvent(SEND_MESSAGE_EVENTS, it) }
    }

    override fun onReadMessageEmit(transferId: Long, messageId: Long) {
        JSONObject().apply {
            put(MessageReadSocketModel.ROOM, transferId)
            put(MessageReadSocketModel.MESSAGE_ID, messageId)
        }.let { socketManager.emitEvent(READ_MESSAGE_EVENTS, it) }
    }

    internal fun onMessageEvent(message: MessageEntity) = eventReceiver.onNewMessage(message)

    internal fun onMessageReadEvent(messageId: Long) = eventReceiver.onMessageRead(messageId)

    internal fun onChatBadgeChangedEvent(chatBadgeEvent: ChatBadgeEventEntity) =
        eventReceiver.onChatBadgeChanged(chatBadgeEvent)
}
