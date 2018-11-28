package com.kg.gettransfer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service

import android.content.Context
import android.content.Intent

import android.os.Binder
import android.os.Build
import android.os.IBinder

import android.support.annotation.CallSuper
import android.support.annotation.RequiresApi

import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager

import com.kg.gettransfer.R

import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket

import io.socket.engineio.client.Socket as EngineSocket
import io.socket.engineio.client.Transport
import io.socket.parser.Packet

import org.json.JSONArray

import timber.log.Timber

/**
 * 
 */
class SocketIOService: Service() {
    private val options = IO.Options().apply {
        path = "/api/socket"
        forceNew = true
    }
    private var socket: Socket? = null
    
    private var url: String? = null
    private var accessToken: String? = null
    private val binder = LocalBinder()
    internal var serviceBinded = false
    
    companion object {
        val DEFAULT_CHANNEL_ID = "${SocketIOService::class.java.name}.offer_service"
        private val NEW_OFFER_RE = Regex("^newOffer/(\\d+)$")
    }
    
    @CallSuper
    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    override fun onBind(intent: Intent): IBinder = binder

    @CallSuper
    override fun onDestroy() {
        socket?.let {
            Timber.d("closeSocketSession [${it.id()}]")
            it.off()
            it.close()
        }
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent) = serviceBinded
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    fun connect(url: String, accessToken: String) {
        /* Reconnect iff URL or token changed. */
        val reconnect = (this.url != url || this.accessToken != accessToken)
        Timber.d("Connecting... options: ${options.path}. Reconect: $reconnect")
        this.url = url
        this.accessToken = accessToken
        if(reconnect) {
            Timber.d("closing previous socket [${socket?.id()}]")
            socket?.off()
            socket?.close()
            socket = null
        }
        if(socket == null) {
            Timber.d("Create socket $url")
            socket = IO.socket(url, options)
            addSocketHandlers()
            socket!!.connect()
        }
    }
    
    private fun addSocketHandlers() {
        with(socket!!.io()) {
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
                        //val offerId = NEW_OFFER_RE.find(event)!!.groupValues.get(1)
                        val item = packet.get(1).toString()
                        val intent = Intent(OfferServiceConnection.INTENT_OFFER)
                        intent.putExtra(OfferServiceConnection.MESSAGE_OFFER, item)
                        LocalBroadcastManager.getInstance(this@SocketIOService).sendBroadcast(intent)
                    }
                }
            }
            on(Socket.EVENT_PING) { _    -> Timber.d("ping [${socket!!.id()}]") }
            on(Socket.EVENT_PONG) { args -> Timber.d("pong ${args.first()}")
                /*
                val intent = Intent(OfferServiceConnection.INTENT_OFFER)
                intent.putExtra(OfferServiceConnection.MESSAGE_OFFER, item)
                LocalBroadcastManager.getInstance(this@SocketIOService).sendBroadcast(intent)
                */
            }
        }
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


    fun isForeground(myPackage: String): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfo = manager.getRunningTasks(1)
        val componentInfo = runningTaskInfo.get(0).topActivity
        return componentInfo.getPackageName().equals("com.kg.gettransfer.presentation.ui")
    }
    */

    inner class LocalBinder: Binder() {
        val service = this@SocketIOService
    }

    private fun retrievePacket(someTrash: Any): JSONArray? {
        Timber.d("someTrash type: ${someTrash::class.qualifiedName}")
        if(someTrash !is Packet<*>) return null
        val packetData: Any? = someTrash.data
        if(packetData == null || packetData !is JSONArray) return null
        return packetData 
    }

                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
    private fun startForeground() {
        val channelId = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel("New Offer Service") else ""

        val notification = NotificationCompat.Builder(this, channelId).apply {
            setOngoing(true)
            setCategory(Notification.CATEGORY_SERVICE)
            setAutoCancel(true)
        }.build()
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground((System.currentTimeMillis() % 10000).toInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelName: String): String {
        val chan = NotificationChannel(DEFAULT_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        chan.setImportance(NotificationManager.IMPORTANCE_MIN)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(chan)
        return DEFAULT_CHANNEL_ID
    }
}
