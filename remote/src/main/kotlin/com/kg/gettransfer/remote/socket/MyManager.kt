package io.socket.client

import com.kg.gettransfer.remote.socket.MySocket

import io.socket.parser.Packet

import java.net.URI

import org.slf4j.LoggerFactory

/**
 * Manager class represents a connection to a given Socket.IO server.
 */
class MyManager(uri: URI, opts: Options): Manager(uri, opts) {
    companion object {
        private val log = LoggerFactory.getLogger("GTR-socket")
    }

    private val _connecting = HashSet<MySocket>()

    /**
     * Initializes [Socket] instances for each namespaces.
     *
     * @param nsp namespace.
     * @param opts options.
     * @return a socket instance for the namespace.
     */
    override fun socket(nsp: String, opts: Options): Socket {
        var socket: Socket? = nsps[nsp]
        if(socket == null) {
            socket = MySocket(this, nsp, opts)
            val _socket = nsps.putIfAbsent(nsp, socket)
            if(_socket != null) socket = _socket
            else {
                socket.on(Socket.EVENT_CONNECTING) { _ -> _connecting.add(socket) }
                socket.on(Socket.EVENT_CONNECT)    { _ -> (if("/" == nsp) "" else "$nsp#") + engine!!.id() }
            }
        }
        return socket
    }
    
    override internal fun packet(packet: Packet<*>) {
        log.debug("packet: $packet")
        super.packet(packet)
    }

    override internal fun destroy(socket: Socket) {
        _connecting.remove(socket)
        if(!_connecting.isEmpty()) return
        close()
    }
}
