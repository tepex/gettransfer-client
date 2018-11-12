package com.kg.gettransfer.data.model

data class ResultEntity<E>(val entity: E? = null, val error: RemoteException? = null)
