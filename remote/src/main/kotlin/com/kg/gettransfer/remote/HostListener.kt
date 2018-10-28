package com.kg.gettransfer.remote

import com.kg.gettransfer.remote.model.EndpointModel

interface HostListener {
    fun onAccessTokenChanged(accessToken: String)
    fun onEndpointChanged(endpoint: EndpointModel, accessToken: String)
}
