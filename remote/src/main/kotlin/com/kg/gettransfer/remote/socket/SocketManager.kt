package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.remote.model.EndpointModel
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.WebSocket
import io.socket.parser.Packet
import kotlinx.serialization.json.JSON
import org.json.JSONArray
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import org.koin.standalone.inject
import org.slf4j.Logger

class SocketManager(): KoinComponent {

    private val log: Logger by inject { parametersOf("GTR-socket") }

    private val offerEventer: OfferEventImpl       by inject()
//    private val transferEventer: TransferEventImpl = get()
//    private val chatEventer: ChatEventImpl         = get()

    private var handler:     OfferModelHandler? = null
    private var socket:      Socket?            = null
    private var url:         String?            = null
    private var accessToken: String?            = null

    var statusOpened:   Boolean = false
    var shouldReconnect         = false

    private val options = IO.Options().apply {
        path        = "/api/socket"
        forceNew    = true
        transports  = arrayOf(WebSocket.NAME)
        timeout     = -1
    }

    fun connect(endpoint: EndpointModel, accessToken: String, handler: OfferModelHandler = {k, l -> }) {
        log.info("connection CcalledYeah")
        statusOpened = true
        this.handler = handler
        connectionChanged(endpoint, accessToken)
    }

    fun disconnect() {
        statusOpened    = false
        shouldReconnect = false
        socket?.let {
            it.off()
            it.close()
        }
    }

    fun connectionChanged(endpoint: EndpointModel, accessToken: String) {
        /* Reconnect iff URL or token changed. */
        //val reconnect = (url != endpoint.url || this.accessToken != accessToken)
        val reconnect = true
        shouldReconnect = true
        url = endpoint.url
        this.accessToken = accessToken
        log.info("FindAccess DS endpoint ${endpoint.url} : accessToken $accessToken")
        if (reconnect) {
            socket?.off()
            socket?.close()

        }
        if (socket == null) startSocket()
    }

    private fun startSocket() {
        socket = IO.socket(url, options)
        addSocketHandlers()
        socket!!.connect()
        log.info("socket connection started")
    }

    private fun addSocketHandlers() {
        with (socket!!.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    @Suppress("UNCHECKED_CAST")
                    var headers = _args.first() as MutableMap<String, List<String>>
                    headers.put("Cookie", listOf("rack.session=$accessToken"))
                }
            }

            on(Manager.EVENT_CONNECT_ERROR) { args -> log.error("socket connect error: $args") }
            on(Manager.EVENT_OPEN) { _ -> log.debug("socket open [${socket?.id()}]") }
            on(Manager.EVENT_CLOSE) { _ -> log.debug("socket close [${socket?.id()}]")
                if (shouldReconnect) {
                    shouldReconnect = false
                    log.debug("socket reconnected ")
                    startSocket() } }
            on(Manager.EVENT_RECONNECTING) { _ -> log.debug("EVENT_RECONNECTING [${socket?.id()}]") }

            on(Manager.EVENT_CONNECT_TIMEOUT) { _ -> log.warn("socket timeout") }
            on(Manager.EVENT_ERROR) { args ->
                val msg = if(args.first() is Exception) (args.first() as Exception).message else args.first().toString()
                log.error("socket error: $msg")
            }
            on(io.socket.engineio.client.Socket.EVENT_PACKET) { args ->
                val packet = retrievePacket(args.first())
                if(packet != null && packet.length() > 1 && packet.get(0) is String) {
                    val event = packet.get(0) as String
                    log.info("packet: ${packet.get(1)}")
                    if(NEW_OFFER_RE.matches(event)) {
                        val mayBeTransferId = NEW_OFFER_RE.find(event)!!.groupValues.get(1)
                        val id = mayBeTransferId.toLongOrNull()
                        if (id != null) onReceiveOffer(id, packet.get(1).toString()) else log.error("Cant parse transfer id: $mayBeTransferId")
                    }
                }
            }
            on(Socket.EVENT_PING) { _    -> log.debug("ping [${socket!!.id()}]") }
            on(Socket.EVENT_PONG) { args -> log.debug("pong ${args.first()}")    }
        }
    }

    private fun retrievePacket(someTrash: Any): JSONArray? {
        log.debug("someTrash type: ${someTrash::class.qualifiedName}")
        if (someTrash !is Packet<*>) return null
        val packetData: Any? = someTrash.data
        if (packetData == null || packetData !is JSONArray) return null
        return packetData
    }
    private fun onReceiveOffer(transferId: Long, offerJson: String) {
        log.debug("onReceiveOffer: $offerJson")
        try {
            val offerEntity = JSON.nonstrict.parse(OfferEntity.serializer(), offerJson).apply { this.transferId = transferId }
            offerEventer.onNewOffer(offerEntity)
  //          val offer = offerMapper.fromEntity(offerEntity)
  //          offer.vehicle.photos = offer.vehicle.photos.map { photo -> systemInteractor.endpoint.url.plus(photo) }
   //         increaseEventsCounter(transferId)
            handler?.invoke(offerJson, transferId)
        } catch (e: Exception) {
            log.error(e.toString())
            throw e
        }
    }

    fun initServerEvent(eventName: String, arg: Any) {
        when {
            socket == null        -> { log.error("event $eventName was not emit: socket is null" ); return }
            !socket!!.connected() -> { log.error("event $eventName was not emit: socket is not connected" ); return }
            else                  -> socket!!.emit(eventName, arg)
        }
    }

    companion object {
        @JvmField val INTENT_OFFER  = "offer"
        @JvmField val MESSAGE_OFFER = "new_offer"
        @JvmField val NEW_OFFER_RE = Regex("^newOffer/(\\d+)$")
        const val ACTION_OFFER = "gt.socket_offerEvent"
    }
}

typealias OfferModelHandler = (String, Long) -> Unit