package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.socket.ChatDataStoreReceiver
import com.kg.gettransfer.data.socket.ChatEventEmitter

class ChatDataStoreIO(private val emitter: ChatEventEmitter): ChatDataStoreReceiver {
    override fun onNewMessage() {

    }
}