package com.kg.gettransfer.service

import com.kg.gettransfer.data.mapper.OfferMapper
import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.SystemListener

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.Offer

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket

import io.socket.engineio.client.Socket as EngineSocket
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.WebSocket
import io.socket.parser.Packet

import kotlinx.serialization.json.JSON

import org.json.JSONArray

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import timber.log.Timber

class OfferServiceConnection : SystemListener, KoinComponent {
    private val options = IO.Options().apply {
        path = "/api/socket"
        forceNew = true
        transports = arrayOf(WebSocket.NAME)
        timeout = -1
    }

    private var handler: OfferModelHandler? = null
    private var socket: Socket? = null
    private var url: String? = null
    private var accessToken: String? = null

    private val systemInteractor: SystemInteractor by inject()
    private val offerMapper: OfferMapper by inject()

    fun connect(endpoint: Endpoint, accessToken: String, handler: OfferModelHandler) {
        this.handler = handler
        systemInteractor.addListener(this)
        connectionChanged(endpoint, accessToken)
    }

    fun disconnect() {
        Timber.d("Unbinding from service")
        systemInteractor.removeListener(this)
        socket?.let {
            Timber.d("closeSocketSession [${it.id()}]")
            it.off()
            it.close()
        }
    }

    override fun connectionChanged(endpoint: Endpoint, accessToken: String) {
        /* Reconnect iff URL or token changed. */
        //val reconnect = (url != endpoint.url || this.accessToken != accessToken)
        val reconnect = true
        Timber.d("Connecting... options: ${options.path}. Reconect: $reconnect")
        url = endpoint.url
        this.accessToken = accessToken
        if (reconnect) {
            Timber.d("closing previous socket [${socket?.id()}]")
            socket?.off()
            socket?.close()
            socket = null
        }
        if (socket == null) {
            Timber.d("Create socket $url")
            socket = IO.socket(url, options)
            addSocketHandlers()
            socket!!.connect()
        }
    }

    private fun addSocketHandlers() {
        with (socket!!.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                Timber.d("event transport. cookie: $accessToken")
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    @Suppress("UNCHECKED_CAST")
                    var headers = _args.first() as MutableMap<String, List<String>>
                    headers.put("Cookie", listOf("rack.session=$accessToken"))
                }
            }

            on(Manager.EVENT_CONNECT_ERROR) { args -> Timber.e("socket connect error: $args") }
            on(Manager.EVENT_OPEN) { _ -> Timber.d("socket open [${socket?.id()}]") }
            on(Manager.EVENT_CLOSE) { _ -> Timber.d("socket close [${socket?.id()}]") }
            on(Manager.EVENT_RECONNECTING) { _ -> Timber.d("EVENT_RECONNECTING [${socket?.id()}]") }

            on(Manager.EVENT_CONNECT_TIMEOUT) { _ -> Timber.w("socket timeout") }
            on(Manager.EVENT_ERROR) { args ->
                val msg = if(args.first() is Exception) (args.first() as Exception).message else args.first().toString()
                Timber.e("socket error: $msg")
            }
            on(EngineSocket.EVENT_PACKET) { args ->
                val packet = retrievePacket(args.first())
                if(packet != null && packet.length() > 1 && packet.get(0) is String) {
                    val event = packet.get(0) as String
                    Timber.i("packet: ${packet.get(1)}")
                    if(NEW_OFFER_RE.matches(event)) {
                        val mayBeTransferId = NEW_OFFER_RE.find(event)!!.groupValues.get(1)
                        val id = mayBeTransferId.toLongOrNull()
                        if (id != null) onReceiveOffer(id, packet.get(1).toString()) else Timber.e("Cant parse transfer id: $mayBeTransferId")
                    }
                }
            }
            on(Socket.EVENT_PING) { _    -> Timber.d("ping [${socket!!.id()}]") }
            on(Socket.EVENT_PONG) { args -> Timber.d("pong ${args.first()}")    }
        }
    }

    private fun retrievePacket(someTrash: Any): JSONArray? {
        Timber.d("someTrash type: ${someTrash::class.qualifiedName}")
        if (someTrash !is Packet<*>) return null
        val packetData: Any? = someTrash.data
        if (packetData == null || packetData !is JSONArray) return null
        return packetData
    }

    private fun onReceiveOffer(transferId: Long, offerJson: String) {
        Timber.d("onReceiveOffer: $offerJson")
        try {
            val offerEntity = JSON.nonstrict.parse(OfferEntity.serializer(), offerJson).apply { this.transferId = transferId }
            val offer = offerMapper.fromEntity(offerEntity)
            offer.vehicle.photos = offer.vehicle.photos.map { photo -> systemInteractor.endpoint.url.plus(photo) }
            increaseEventsCounter(transferId)
            handler?.invoke(offer)
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        }
    }

    private fun increaseEventsCounter(transferId: Long) {
        var count = systemInteractor.eventsCount
        systemInteractor.eventsCount = ++count

        var transferIds = systemInteractor.transferIds
        transferIds = transferIds.toMutableList().apply { add(transferId) }
        systemInteractor.transferIds = transferIds
    }


    companion object {
        @JvmField val INTENT_OFFER  = "offer"
        @JvmField val MESSAGE_OFFER = "new_offer"
        @JvmField val NEW_OFFER_RE = Regex("^newOffer/(\\d+)$")
    }
}

typealias OfferModelHandler = (Offer) -> Unit
