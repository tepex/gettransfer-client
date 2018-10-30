package com.kg.gettransfer.remote

import com.google.gson.GsonBuilder

import com.kg.gettransfer.data.OfferSocket
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.OfferEntityListener

//import com.kg.gettransfer.remote.mapper.OfferMapper

import com.kg.gettransfer.remote.model.EndpointModel
import com.kg.gettransfer.remote.model.OfferModel

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket

import io.socket.engineio.client.Socket as EngineSocket
import io.socket.engineio.client.Transport

import io.socket.parser.Packet

import org.json.JSONArray

import org.slf4j.LoggerFactory

/**
 * host = "https://gettransfer.com"
 * logging: https://proandroiddev.com/managing-logging-in-a-multi-module-android-application-eb966fb7fedc
 */
class OfferSocketImpl(): OfferSocket, HostListener {
    private val log = LoggerFactory.getLogger("GTR-socket")
    private val gson = GsonBuilder().create()
    
    private var listener: OfferEntityListener? = null
    private var socket: Socket? = null
    private var endpoint: EndpointModel? = null
    private var accessToken: String? = null
    
    companion object {
        @JvmField val PATH  = "/api/socket"
        private val NEW_OFFER_RE = Regex("^newOffer/(\\d+)$")
    }
    
    override fun setListener(listener: OfferEntityListener) {
        this.listener = listener
        log.debug("set listener")
        if(socket == null) connect()
    }
    
    override fun removeListener(listener: OfferEntityListener) {
        this.listener = null
        disconnect()
    }
    
    override fun onAccessTokenChanged(accessToken: String) {
        this.accessToken = accessToken
        socket?.let { reconnect() }
    }
    
    override fun onEndpointChanged(endpoint: EndpointModel, accessToken: String) {
        this.endpoint = endpoint
        this.accessToken = accessToken
        socket?.let { reconnect() }
    }
    
    private fun connect() {
        if(endpoint == null || accessToken == null) {
            log.error("endpoint or accessToken are not initialized!")
            return
        }
        
        log.debug("start connect to ${endpoint!!.url}. token: $accessToken")
        val options = IO.Options().apply {
            path = PATH
            forceNew = true
        }
        log.debug("options: ${options.path}")
        
        socket = IO.socket(endpoint!!.url, options)
        with(socket!!.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                log.debug("event transport. cookie: $accessToken")
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    var headers = _args.first() as MutableMap<String, List<String>>
                    headers.put("Cookie", listOf("rack.session=$accessToken"))
                }
            }
            
            on(Manager.EVENT_CONNECT_ERROR) { args -> log.error("socket connect error: $args") }
            on(Manager.EVENT_OPEN) { _ -> log.debug("socket open") }
            on(Manager.EVENT_CLOSE) { _ -> log.debug("socket close") }
            on(Manager.EVENT_RECONNECTING) { _ -> log.debug("EVENT_RECONNECTING") }
            
            on(Manager.EVENT_CONNECT_TIMEOUT) { _ ->
                log.warn("socket timeout")
                listener?.let { it.onError(RemoteException(RemoteException.CONNECTION_TIMED_OUT, "Socket ${endpoint!!.url} timeout")) }
            }
            on(Manager.EVENT_ERROR) { args ->
                val msg = if(args.first() is Exception) (args.first() as Exception).message else args.first().toString()
                log.error("socket error: $msg")
                listener?.let { it.onError(RemoteException(RemoteException.INTERNAL_SERVER_ERROR, msg!!)) }
            }
            on(EngineSocket.EVENT_PACKET) { args ->
                val packet = args.first() as Packet<JSONArray>
                if(packet.data != null && packet.data.length() > 1 && packet.data.get(0) is String) {
                    val event = packet.data.get(0) as String
                    if(NEW_OFFER_RE.matches(event)) {
                        val offerId = NEW_OFFER_RE.find(event)!!.groupValues.get(1)
                        val item = packet.data.get(1)
                        log.debug("OfferSocketImpl.Data: ${item::class.qualifiedName} $item")
                    }
                }
                /*
                if(args.first() !is JSONObject) listener.onError(SocketException(SocketException.INTERNAL_SERVER_ERROR))
                else {
                    val offerModel = gson.fromJson(args.first() as JSONObject, OfferModel::class.java)
                    listener.onNewOffer(mapper.fromRemote(offerModel))
                }
                */
            }
        }
        socket!!.connect()
    }
    
    private fun disconnect() {
        socket?.let { it.disconnect() }
        socket = null
    }
    
    private fun reconnect() {
        disconnect()
        connect()
    }
}
