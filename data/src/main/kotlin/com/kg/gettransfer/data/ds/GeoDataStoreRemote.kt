package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.GeoRemote
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

open class GeoDataStoreRemote : KoinComponent {
    private val remote: GeoRemote by inject()

    suspend fun getMyLocation() = remote.getMyLocation()
}