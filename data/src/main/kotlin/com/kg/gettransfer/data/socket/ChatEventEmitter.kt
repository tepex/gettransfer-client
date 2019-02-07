package com.kg.gettransfer.data.socket

interface ChatEventEmitter {
    fun onJoinRoomEmit(transferId: Long)
    fun onLeaveRoomEmit(transferId: Long)
    fun onMessageEmit()
}