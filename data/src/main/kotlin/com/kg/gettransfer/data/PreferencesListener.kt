package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.EndpointEntity

interface PreferencesListener {
    fun accessTokenChanged(accessToken: String)
    fun endpointChanged(endpointEntity: EndpointEntity)
}
