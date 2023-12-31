package com.kg.gettransfer.data.model

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RemoteException

data class ResultEntity<E>(
    val entity: E? = null,
    val error: RemoteException? = null,
    val cacheError: CacheException? = null
)
