package com.kg.gettransfer.remote.model

import com.google.gson.annotations.Expose
import com.kg.gettransfer.data.model.EndpointEntity

data class EndpointModel(
    @Expose val key: String,
    @Expose val url: String
)

fun EndpointEntity.map() = EndpointModel(key, url)
