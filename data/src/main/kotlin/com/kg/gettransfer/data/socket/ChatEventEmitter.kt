package com.kg.gettransfer.data.socket

interface ChatEventEmitter {

    fun onJoinRoomEmit(transferId: Long)

    fun onLeaveRoomEmit(transferId: Long)

    fun onSendMessageEmit(transferId: Long, text: String): Boolean

    fun onReadMessageEmit(transferId: Long, messageId: Long)
}
