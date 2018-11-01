package com.kg.gettransfer.presentation.ui

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection

import android.os.IBinder

import com.kg.gettransfer.service.SocketIOService

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel

class OfferServiceConnection(private val context: Context): BroadcastReceiver(), ServiceConnection {
    private lateinit var socketService: SocketIOService
    private lateinit var handler: OfferModelHandler 
    
    fun connect(handler: OfferModelHandler) {
        this.handler = handler
        context.bindService(Intent(context, SocketIOService::class.java), this, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(context).registerReceiver(this, IntentFilter(SocketIOService.MESSAGE_OFFER))
    }
    
    fun disconnect() {
        context.unbindService(this)
        socketService.disconnect()
    }
    
    fun connectSocket(url: String, accessToken: String) {
        socketService.reconnect(url, accessToken)
    }
    
    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        socketService = (service as SocketIOService.LocalBinder).service
    }
    
    override fun onServiceDisconnected(name: ComponentName) {}
    
    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra(SocketIOService.MESSAGE_OFFER)?.let { handler(Mappers.getNewOfferModel(it)) }
    }
}

typealias OfferModelHandler = (OfferModel) -> Unit