package com.kg.gettransfer.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection

import android.os.IBinder

import android.support.v4.content.LocalBroadcastManager

import com.kg.gettransfer.data.mapper.OfferMapper
import com.kg.gettransfer.data.model.OfferEntity

import com.kg.gettransfer.domain.SystemListener
import com.kg.gettransfer.domain.model.Endpoint
import com.kg.gettransfer.domain.model.Offer

import kotlinx.serialization.json.JSON

import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import timber.log.Timber

class OfferServiceConnection: BroadcastReceiver(), ServiceConnection, SystemListener, KoinComponent {
    companion object {
        @JvmField val INTENT_OFFER  = "offer"
        @JvmField val MESSAGE_OFFER = "new_offer"
    }
    
    private lateinit var url: String
    private lateinit var accessToken: String
    
    private var socketService: SocketIOService? = null
    private var handler: OfferModelHandler? = null
    
    private val offerMapper: OfferMapper by inject()
    
    fun connect(context: Context, handler: OfferModelHandler) {
        this.handler = handler
        Timber.d("Binding service ${context::class.qualifiedName}")
        context.bindService(Intent(context, SocketIOService::class.java), this, Context.BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(context).registerReceiver(this, IntentFilter(INTENT_OFFER))
    }
    
    fun disconnect(context: Context) {
        Timber.d("Unbinding from service ${context::class.qualifiedName}")
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
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
        socketService = null
        handler = null
        Timber.d("OSC.onServiceDisconnected")
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val offerEntity = intent.getStringExtra(MESSAGE_OFFER)
        Timber.d("onReceive: $offerEntity")
        try {
            val offer = offerMapper.fromEntity(JSON.parse(OfferEntity.serializer(), offerEntity))
            handler?.invoke(offer)
        } catch(e: Exception) {
            Timber.e(e)
            throw e
        }
    }
}

typealias OfferModelHandler = (Offer) -> Unit
