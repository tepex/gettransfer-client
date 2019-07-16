package com.kg.gettransfer.data.repository

import com.kg.gettransfer.core.domain.Repository

import com.kg.gettransfer.domain.repository.SystemRepository

import com.kg.gettransfer.sys.domain.MobileConfigs

import org.koin.core.KoinComponent
import org.koin.core.inject

class MobileConfigsRepositoryImpl : Repository<MobileConfigs>, KoinComponent {

    private val systemRepository: SystemRepository by inject()

    override fun get() = systemRepository.mobileConfigs
}
