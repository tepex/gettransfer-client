package com.kg.gettransfer.domain

import com.kg.gettransfer.domain.model.Endpoint

interface SystemListener {
    fun connectionChanged(endpoint: Endpoint, accessToken: String)
}
