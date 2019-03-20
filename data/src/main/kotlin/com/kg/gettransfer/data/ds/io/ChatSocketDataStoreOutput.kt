package com.kg.gettransfer.data.ds.io

import com.kg.gettransfer.data.socket.ChatEventEmitter

class ChatSocketDataStoreOutput(private val emitter: ChatEventEmitter) {

    fun onJoinRoomEmit(transferId: Long) = emitter.onJoinRoomEmit(transferId)
    fun onLeaveRoomEmit(transferId: Long) = emitter.onLeaveRoomEmit(transferId)
    fun onSendMessageEmit(transferId: Long, text: String) = emitter.onSendMessageEmit(transferId, text)
    fun onReadMessageEmit(transferId: Long, messageId: Long) = emitter.onReadMessageEmit(transferId, messageId)
}