package com.kg.gettransfer.remote.socket

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.model.ChatBadgeEventEntity
import com.kg.gettransfer.data.model.CoordinateEntity
import com.kg.gettransfer.data.model.MessageEntity
import com.kg.gettransfer.data.model.OfferEntity
import com.kg.gettransfer.data.model.PaymentStatusEventEntity

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.WebSocket
import io.socket.parser.Packet

import kotlinx.serialization.json.JSON

import org.json.JSONArray
import org.koin.core.parameter.parametersOf
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

import org.slf4j.Logger

@Suppress("TooManyFunctions")
class SocketManager : KoinComponent {

    private val log: Logger by inject { parametersOf("GTR-socket") }

    private val offerEventer: OfferSocketImpl         by inject()
    private val transferEventer: CoordinateSocketImpl by inject()
    private val chatEventer: ChatSocketImpl           by inject()
    private val systemEventer: SystemSocketImp        by inject()
    private val paymentEventer: PaymentSocketEventer  by inject()

    private val preferences = get<PreferencesCache>()

    private var socket:      Socket? = null
    private var url:         String? = null
    private var accessToken: String? = null

    private var statusOpened    = false
    private var shouldReconnect = false

    @Suppress("MagicNumber")
    private val options = IO.Options().apply {
        path        = "/api/socket"
        forceNew    = true
        transports  = arrayOf(WebSocket.NAME)
        timeout     = 2000
        reconnectionAttempts = 5
        reconnectionDelay    = 2000
    }

    fun changeEndpoint(url: String) {
        this.url = url
    }

    fun startConnection() {
        prepareSocket(statusOpened)
    }

    fun changeConnection() {
        if (statusOpened) prepareSocket(true)
    }

    private fun prepareSocket(withReconnect: Boolean) {
        this.accessToken = preferences.accessToken
        if (withReconnect) disconnect(true) else openSocket()
    }

    private fun openSocket() {
        val s = IO.socket(url, options)
        addSocketHandlers(s)
        s.connect()
        socket = s
        log.info("$SOCKET_TAG connection started")
    }

    fun disconnect(withReconnect: Boolean) {
        shouldReconnect = withReconnect
        socket?.let { s ->
            s.off()
            s.close()
        }
    }

    @Suppress("LongMethod", "ComplexMethod")
    private fun addSocketHandlers(socket: Socket) {
        with(socket.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                @Suppress("UNCHECKED_CAST")
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    @Suppress("UNCHECKED_CAST")
                    val headers = _args.first() as MutableMap<String, List<String>>
                    headers["Cookie"] = listOf("rack.session=$accessToken")
                }
            }
            on(Manager.EVENT_CONNECT_ERROR) { args -> log.error("$SOCKET_TAG connect error: $args") }
            on(Manager.EVENT_OPEN) {
                log.debug("$SOCKET_TAG open [${socket.id()}]")
                statusOpened = true
                systemEventer.onConnected()
            }
            on(Manager.EVENT_CLOSE) {
                log.debug("$SOCKET_TAG close [${socket.id()}]")
                statusOpened = false
                if (shouldReconnect) {
                    shouldReconnect = false
                    log.debug("$SOCKET_TAG reconnected ")
                    openSocket()
                } else {
                    systemEventer.onDisconnected()
                }
            }
            on(Manager.EVENT_RECONNECTING) { log.debug("EVENT_RECONNECTING [${socket.id()}]") }
            on(Manager.EVENT_CONNECT_TIMEOUT) { log.warn("$SOCKET_TAG timeout") }
            on(Manager.EVENT_ERROR) { args ->
                @Suppress("UNCHECKED_CAST")
                val m = if (args.first() is Exception) (args.first() as Exception).message else args.first().toString()
                log.error("$SOCKET_TAG error: $m")
            }
            on(io.socket.engineio.client.Socket.EVENT_PACKET) { args ->
                val packet = retrievePacket(args.first())
                if (packet != null && packet.length() > 1 && packet.get(0) is String) {
                    @Suppress("UNCHECKED_CAST")
                    val event = packet.get(0) as String
                    log.info("packet: ${packet.get(1)}")
                    when {
                        NEW_OFFER_RE.matches(event) -> {
                            val mayBeTransferId = NEW_OFFER_RE.find(event)?.groupValues?.get(1)
                            val id = mayBeTransferId?.toLongOrNull()
                            if (id != null) {
                                onReceiveOffer(id, packet.get(1).toString())
                            } else {
                                log.error("Cant parse transfer id: $mayBeTransferId")
                            }
                        }
                        NEW_LOCATION_RE.matches(event) -> {
                            val id = event.split("/").last().toLongOrNull()
                            if (id != null) {
                                onReceiveLocation(id, packet[1].toString())
                            } else {
                                log.error("Cant parse location transferId: $id")
                            }
                        }

                        NEW_MESSAGE_RE.matches(event)    -> onReceiveMessage(packet[1].toString())
                        MESSAGE_READ_RE.matches(event)   -> onReceiveMessageRead(packet[1].toString().toLong())
                        CHAT_BADGE_RE.matches(event)     -> onReceiveChatBadge(packet[1].toString())
                        PAYMENT_STATUS_RE.matches(event) -> onReceiveNewPaymentStatus(packet[1].toString())
                    }
                }
            }
            on(Socket.EVENT_PING) { log.debug("ping [${socket.id()}]") }
            on(Socket.EVENT_PONG) { args -> log.debug("pong ${args.first()}")    }
        }
    }

    private fun retrievePacket(someTrash: Any): JSONArray? {
        return if (someTrash is Packet<*>) {
            val packetData: Any? = someTrash.data
            if (packetData == null || packetData !is JSONArray) null else packetData
        } else {
            null
        }
    }

    private fun onReceiveOffer(transferId: Long, offerJson: String) {
        log.info("$SOCKET_TAG onReceiveOffer: $offerJson")
        tryParse {
            JSON.nonstrict.parse(OfferEntity.serializer(), offerJson)
                .apply { this.transferId = transferId }
                .also { offerEventer.onNewOffer(it) }
        }
    }

    private fun onReceiveLocation(mTransferId: Long, coordinatesJson: String) {
        log.info("$SOCKET_TAG onLocation: $coordinatesJson")
        tryParse {
            JSON.nonstrict.parse(CoordinateEntity.serializer(), coordinatesJson)
                .apply { transferId = mTransferId }
                .also { transferEventer.onLocationUpdated(it) }
        }
    }

    private fun onReceiveMessage(messageJson: String) {
        log.debug("$SOCKET_TAG onMessage: $messageJson")
        try {
            val messageEntity = JSON.nonstrict.parse(MessageEntity.serializer(), messageJson)
            chatEventer.onMessageEvent(messageEntity)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            log.error(e.toString())
            throw e
        }
    }

    private fun onReceiveMessageRead(messageId: Long) {
        log.debug("$SOCKET_TAG onMessageRead: $messageId")
        try {
            chatEventer.onMessageReadEvent(messageId)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            log.error(e.toString())
            throw e
        }
    }

    private fun onReceiveChatBadge(messageJson: String) {
        log.debug("$SOCKET_TAG onChatBadge: $messageJson")
        try {
            val chatBadge = JSON.nonstrict.parse(ChatBadgeEventEntity.serializer(), messageJson)
            chatEventer.onChatBadgeChangedEvent(chatBadge)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            log.error(e.toString())
            throw e
        }
    }

    private fun onReceiveNewPaymentStatus(messageJson: String) {
        log.debug("$SOCKET_TAG onPaymentStatus: $messageJson")
        try {
            val event = JSON.nonstrict.parse(PaymentStatusEventEntity.serializer(), messageJson)
            paymentEventer.onNewPaymentStatusEvent(event.isPaymentSuccessed())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            log.error(e.toString())
            throw e
        }
    }

    private fun <T> tryParse(block: () -> T): T {
        return try {
            block.invoke()
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            log.error("can't parse data: see previous log note")
            throw e
        }
    }

    fun emitEvent(eventName: String, arg: Any?, sendAfterReconnected: Boolean = false): Boolean {
        return when {
            socket == null -> {
                log.error("event $eventName was not emit: $SOCKET_TAG is null")
                false
            }
            !statusOpened && !sendAfterReconnected -> {
                log.error("event $eventName was not emit: $SOCKET_TAG is not connected")
                false
            }
            else -> {
                socket?.emit(eventName, arg)
                log.error("event $eventName was emit: $SOCKET_TAG")
                true
            }
        }
    }

    fun emitAck(event: String, arg: Array<out Any>) {
        when (socket) {
            null -> {
                log.error("event $event was not emit: $SOCKET_TAG is null")
                return
            }
            else -> {
                socket?.emit(event, arg) { log.info("Event received") }
                log.info("SOCKET_MANAGER: $event emit - $arg")
            }
        }
    }

    companion object {
        const val SOCKET_TAG = "socketManager"
        @JvmField val NEW_OFFER_RE      = Regex("^newOffer/(\\d+)$")
        @JvmField val NEW_LOCATION_RE   = Regex("^carrier-position/(\\d+)$")
        @JvmField val NEW_MESSAGE_RE    = Regex("^chat.new-message$")
        @JvmField val MESSAGE_READ_RE   = Regex("^chat.message-read$")
        @JvmField val CHAT_BADGE_RE     = Regex("^chat.set-badge$")
        @JvmField val PAYMENT_STATUS_RE = Regex("^payment/(\\d+)$")
    }
}
