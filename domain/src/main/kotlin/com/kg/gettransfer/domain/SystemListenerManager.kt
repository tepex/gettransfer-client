package com.kg.gettransfer.domain

import com.kg.gettransfer.domain.SystemListener

interface SystemListenerManager {
    fun addListener(listener: SystemListener)
    fun removeListener(listener: SystemListener)
}
