package com.kg.gettransfer.modules

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import org.koin.standalone.KoinComponent

/**
 * Created by denisvakulenko on 09/02/2018.
 */

object GoogleApiClientFactory : KoinComponent {
    fun create(context: Context): GoogleApiClient {
        val mGoogleApiClient = GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
//            .enableAutoManage(context, GOOGLE_API_CLIENT_ID, context)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(p0: Bundle?) {
                    }

                    override fun onConnectionSuspended(p0: Int) {
                    }
                })
                .build()

        mGoogleApiClient.connect()

        return mGoogleApiClient
    }
}