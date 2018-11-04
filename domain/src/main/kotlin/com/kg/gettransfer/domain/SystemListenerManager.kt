package com.kg.gettransfer.domain

import com.kg.gettransfer.domain.SystemListener

interface SystemListenerManager {
    fun addSystemListener(listener: SystemListener)
    fun removeSystemListener(listener: SystemListener)
}
