package com.kg.gettransfer.data.ds.IO

import com.kg.gettransfer.data.repository.SystemRepositoryImpl
import com.kg.gettransfer.data.socket.SystemDataStoreReceiver
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class SystemSocketDataStoreInput: KoinComponent, SystemDataStoreReceiver {
    private val repository: SystemRepositoryImpl by inject()

    override fun socketConnected() = repository.notifyAboutConnection()
    override fun socketDisconnected() = repository.notifyAboutDisconnection()
}