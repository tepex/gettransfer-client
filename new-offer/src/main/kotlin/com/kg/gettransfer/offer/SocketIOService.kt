package com.kg.gettransfer.offer

import android.app.Service

import android.content.Intent

import android.os.Binder
import android.os.IBinder

import android.support.annotation.CallSuper

import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.offer.mapper.OfferMapper

import com.kg.gettransfer.remote.HostListener
import com.kg.gettransfer.remote.model.EndpointModel

import timber.log.Timber

/**
https://github.com/Mahabali/Socket.io-Android-Chat
*/
class SocketIOService(private val mapper: OfferMapper): Service(), OfferSocket, HostListener {
    private var socket: Socket? = null
    private lateinit var endpoint: EndpointModel
    private lateinit var accessToken: String
    
    private val binder = LocalBinder()
    
    var serviceBinded = false
        private get
    
    companion object {
        @JvmField val PATH  = "/api/socket"
        private val NEW_OFFER_RE = Regex("^newOffer/(\\d+)$")
    }
    
    override fun onBind(intent: Intent): IBinder = binder

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        initializeSocket()
        addSocketHandlers()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        closeSocketSession()
    }

    override fun onUnbind(intent: Intent) = serviceBinded
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int) = START_STICKY
    
    private fun initializeSocket() {
        val options = IO.Options().apply {
            path = PATH
            forceNew = true
        }
        Timber.d("options: ${options.path}")
        socket = IO.socket(endpoint.url, options)
    }

    private fun closeSocketSession() {
        socket.disconnect()
        socket.off()
    }

    private fun addSocketHandlers() {
        with(socket.io()) {
            on(Manager.EVENT_TRANSPORT) { args ->
                Timber.d("event transport. cookie: $accessToken")
                val transport = args.first() as Transport
                transport.on(Transport.EVENT_REQUEST_HEADERS) { _args ->
                    var headers = _args.first() as MutableMap<String, List<String>>
                    headers.put("Cookie", listOf("rack.session=$accessToken"))
                }
            }
            
            on(Manager.EVENT_CONNECT_ERROR) { args -> Timber.e("socket connect error: $args") }
            on(Manager.EVENT_OPEN) { _ -> Timber.d("socket open") }
            on(Manager.EVENT_CLOSE) { _ -> Timber.d("socket close") }
            on(Manager.EVENT_RECONNECTING) { _ -> Timber.d("EVENT_RECONNECTING") }
            
            on(Manager.EVENT_CONNECT_TIMEOUT) { _ ->
                Timber.w("socket timeout")
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
                
                if (isForeground("com.example.mahabali.socketiochat")) {
                    val intent = Intent(SocketEventConstants.newMessage)
                    intent.putExtra("username", username)
                    intent.putExtra("message", message)
                    LocalBroadcastManager.getInstance(this@SocketIOService).sendBroadcast(intent)
                } else {
                    showNotificaitons(username, message)
                }
                
                
                */
            }
        }
        socket.connect()
    }
    
    fun connect() {
        socket.connect()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun restartSocket() {
        socket.off()
        socket.disconnect()
        addSocketHandlers()
    }
    
    /*
    fun showNotificaitons(username: String, message: String) {
        val toLaunch = Intent(getApplicationContext(), MainActivity::class.java)
        toLaunch.putExtra("username", message)
        toLaunch.putExtra("message", message)
        toLaunch.setAction("android.intent.action.MAIN")
        val pIntent = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val n = NotificationCompat.Builder(this)
                .setContentTitle("You have pending new messages")
                .setContentText("New Message")
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_hdp)
                .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        notificationManager.notify(0, n)
    }
    */

    fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo.get(0).topActivity
        return componentInfo.getPackageName().equals("com.kg.gettransfer.presentation.ui")
    }
    
    inner class LocalBinder: Binder() {
        val service = this@SocketIOService
    }
}
