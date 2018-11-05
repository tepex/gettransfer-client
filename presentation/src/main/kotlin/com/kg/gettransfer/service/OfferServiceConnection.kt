package com.kg.gettransfer.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection

import android.os.IBinder

import android.support.v4.content.LocalBroadcastManager

import com.kg.gettransfer.domain.SystemListener
import com.kg.gettransfer.domain.model.Endpoint

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel

import timber.log.Timber

class OfferServiceConnection(private val context: Context): BroadcastReceiver(), ServiceConnection, SystemListener {
    private var socketService: SocketIOService? = null
    private lateinit var url: String
    private lateinit var accessToken: String
    
    private lateinit var handler: OfferModelHandler
    
    fun connect(handler: OfferModelHandler) {
        this.handler = handler
        context.bindService(Intent(context, SocketIOService::class.java), this, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(context).registerReceiver(this, IntentFilter(SocketIOService.MESSAGE_OFFER))
    }
    
    fun disconnect() {
        Timber.d("Disconnect from service") 
        context.unbindService(this)
    }
    
    override fun connectionChanged(endpoint: Endpoint, accessToken: String) {
        url = endpoint.url
        this.accessToken = accessToken
        socketService?.let { it.connect(url, accessToken) }
    }
    
    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        Timber.d("OSC.onServiceConnected")
        socketService = (service as SocketIOService.LocalBinder).service
        socketService!!.serviceBinded = true
        socketService!!.connect(url, accessToken)
    }
    
    override fun onServiceDisconnected(name: ComponentName) {
        socketService!!.serviceBinded = false
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive: ${intent.getStringExtra(SocketIOService.MESSAGE_OFFER)}")
        //intent.getStringExtra(SocketIOService.MESSAGE_OFFER)?.let { handler(Mappers.getNewOfferModel(it)) }
    }
}

typealias OfferModelHandler = (OfferModel) -> Unit