package com.kg.gettransfer.presentation

import android.net.ConnectivityManager
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.content.LocalBroadcastManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.util.Log


class NetworkStateChangeReceiver : BroadcastReceiver() {

    /*private var listener: OnNetworkStateReceivesListener? = null

    fun setOnNetworkStateReceivedListener(context: Context){
        this.listener = context as OnNetworkStateReceivesListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (listener != null) {
            listener!!.onNetworkStateReceive(activeNetwork != null && activeNetwork.isConnected)
        }
    }*/

    companion object {
        const val NETWORK_AVAILABLE_ACTION = "com.kg.gettransfer.NetworkAvailable"
        const val IS_NETWORK_AVAILABLE = "isNetworkAvailable"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val networkStateIntent = Intent(NETWORK_AVAILABLE_ACTION)
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConnectedToInternet(context))
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent)
    }

    private fun isConnectedToInternet(context: Context?): Boolean {
        try {
            if (context != null) {
                val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
            return false
        } catch (e: Exception) {
            //Log.e(NetworkStateChangeReceiver::class.java!!.getName(), e.message)
            return false
        }

    }
}

/*
interface OnNetworkStateReceivesListener {
    fun onNetworkStateReceive(isAvailable: Boolean)
}*/
