package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.ChatEventEmitter
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class ChatEventImpl: ChatEventEmitter, KoinComponent {
    private val receiver: ChatDataStoreReceiver = get()
    private val socket: SocketManager = get()

    override fun onMessageEmit(){}
    fun onMessageEvent() = receiver.onNewMessage()

}