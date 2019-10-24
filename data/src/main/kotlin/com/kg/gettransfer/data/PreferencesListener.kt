package com.kg.gettransfer.data

import com.kg.gettransfer.sys.data.EndpointEntity

interface PreferencesListener {
    fun accessTokenChanged(accessToken: String)
    fun endpointChanged(endpointEntity: EndpointEntity)
}
