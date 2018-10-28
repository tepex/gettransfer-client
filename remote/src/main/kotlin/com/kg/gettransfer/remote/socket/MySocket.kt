package com.kg.gettransfer.remote.socket

import io.socket.client.Manager
import io.socket.client.MyManager
import io.socket.client.Socket

import io.socket.emitter.Emitter

import org.slf4j.LoggerFactory

class MySocket(val myIo: MyManager, nsp: String, val opts: Manager.Options): Socket(myIo, nsp, opts) {
    companion object {
        private val log = LoggerFactory.getLogger("GTR-socket")
    }
    
    /**
     * Emits an event. When you pass [Ack] at the last argument, then the acknowledge is done.
     *
     * [event] an event name.
     * [args] data to send.
     * @return a reference to this object.
     */
    override fun emit(event: String, vararg args: Any?): Emitter {
        log.debug("emit: $event ${args.firstOrNull()}")
        log.debug("from emit", Exception())
        return super@MySocket.emit(event, args)
    }
}
