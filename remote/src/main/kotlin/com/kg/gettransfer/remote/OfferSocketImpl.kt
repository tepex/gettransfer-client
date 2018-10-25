package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder

import com.kg.gettransfer.data.OfferSocket
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.OfferEntityListener

//import com.kg.gettransfer.remote.mapper.OfferMapper

import com.kg.gettransfer.remote.model.OfferModel

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket

import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport

import org.slf4j.LoggerFactory

/**
 * host = "https://gettransfer.com"
 * logging: https://proandroiddev.com/managing-logging-in-a-multi-module-android-application-eb966fb7fedc
 */
class OfferSocketImpl(private var accessToken: String,
                      private val host: String): OfferSocket, AccessTokenListener {
    private val log = LoggerFactory.getLogger("GTR-socket")
    private val gson = GsonBuilder().create()
    
    private var listener: OfferEntityListener? = null
    private var socket: Socket? = null
    
    companion object {
        @JvmField val PATH  = "/api/socket"
        @JvmField val EVENT = "newOffer"
    }
    
    override fun setListener(listener: OfferEntityListener) {
        this.listener = listener
        socket?.also { reconnect() }
    }
    
    override fun removeListener(listener: OfferEntityListener) {
        this.listener = null
        disconnect()
    }
    
    override fun onAccessTokenChanged(accessToken: String) {
        this.accessToken = accessToken
        reconnect()
    }
    
    private fun connect() {
        val options = IO.Options().apply {
            path = PATH
            forceNew = true
            transports
        }
        socket = IO.socket(host, options)
        with(socket!!.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    var headers = _args.first() as MutableMap<String, List<String>>
                    headers.put("Cookie", listOf("rack.session=$accessToken"))
                }
            }
            
            /*
            on(Manager.EVENT_CONNECT_ERROR, onConnectEvent)
            on(Manager.EVENT_OPEN, onOpenEvent)
            on(Manager.EVENT_CLOSE) { args -> Timber.d("close") }
            on(Manager.EVENT_RECONNECTING) { args -> Timber.d("EVENT_RECONNECTING") }
            on(Socket.EVENT_MESSAGE, onNewOffer)
            */
            
            on(Manager.EVENT_CONNECT_TIMEOUT) { _ ->
                listener?.let { it.onError(RemoteException(RemoteException.CONNECTION_TIMED_OUT, "Socket $host timeout")) }
            }
            on(Manager.EVENT_ERROR) { args ->
                val msg = if(args.first() is Exception) (args.first() as Exception).message else args.first().toString()
                listener?.let { it.onError(RemoteException(RemoteException.INTERNAL_SERVER_ERROR, msg!!)) }
            }
            on(EVENT) { args ->
                /*
                if(args.first() !is JSONObject) listener.onError(SocketException(SocketException.INTERNAL_SERVER_ERROR))
                else {
                    val offerModel = gson.fromJson(args.first() as JSONObject, OfferModel::class.java)
                    listener.onNewOffer(mapper.fromRemote(offerModel))
                }
                */
                val arg = args.first()
                log.debug("type: ${arg::class.qualifiedName}")
                log.debug("new offer: $arg")
            }
        }
        socket!!.connect()
    }
    
    private fun disconnect() {
        socket?.apply {
            off(EVENT)
            disconnect()
        }
        socket = null
    }
    
    private fun reconnect() {
        disconnect()
        connect()
    }
}
