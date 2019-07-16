package com.kg.gettransfer.data

import com.kg.gettransfer.data.model.ConfigsEntity

import com.kg.gettransfer.sys.data.MobileConfigsEntity

import org.koin.core.KoinComponent

interface SystemCache : KoinComponent {

    fun getConfigs(): ConfigsEntity?

    fun setConfigs(configs: ConfigsEntity)

    fun getMobileConfigs(): MobileConfigsEntity?

    fun setMobileConfigs(configs: MobileConfigsEntity)
}
