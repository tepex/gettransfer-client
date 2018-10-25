package com.kg.gettransfer.geo

import com.kg.gettransfer.data.OfferSocket
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket

import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport

/**
 * host = "https://gettransfer.com"
 * path = "/api/socket"
 * logging: https://proandroiddev.com/managing-logging-in-a-multi-module-android-application-eb966fb7fedc
 */
class OfferSocketImpl(private val host: String, private val apiPath: String): OfferSocket {
    private lateinit var listener: OfferListener
    private var socket: Socket? = null
    
    companion object {
        @JvmField val EVENT          = "newOffer"
    }
    
    fun connect(listener: OfferListener, accessToken: String) {
        socket?.also { disconnect() }
        this.listener = listener
        
        val options = IO.Options().apply {
            path = apiPath
            forceNew = true
            transports
        }
        socket = IO.socket(host, options).io().apply {
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
                listener.onError(ApiException(ApiException.CONNECTION_TIMEOUT, "Socket $host timeout"))
            }
            on(Manager.EVENT_ERROR) { args ->
                val msg = if(args.first() is Exception) args.first().message else args.first().toString()
                listener.onError(ApiException(ApiException.INTERNAL_SERVER_ERROR, msg)
            }
            on(EVENT) { args ->
                if(args.first() !is JSONObject) listener.onError(SocketException(SocketException.INTERNAL_SERVER_ERROR))
                else {
                    val json = args.first() as JSONObject
                    offerMapper.fromEntity(Gson().fromJson(offer, OfferEntity::class.java)), systemInteractor.locale))
                }
                
            }
        }.connect()
    }
    
    fun disconnect() {
        socket?.apply {
            off(EVENT)
            disconnect()
        }
        socket = null
    }
    
    private 
}
