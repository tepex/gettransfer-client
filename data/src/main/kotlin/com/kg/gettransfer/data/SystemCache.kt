package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.MobileConfigEntity
import org.koin.core.KoinComponent

interface SystemCache : KoinComponent {

    fun getMobileConfigs(): MobileConfigEntity?

    fun setMobileConfigs(configs: MobileConfigEntity)
}
