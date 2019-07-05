package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.PassengerAccount

import com.kg.gettransfer.presentation.model.PassengerAccountModel

import com.kg.gettransfer.presentation.ui.SystemUtils

import org.koin.core.get

open class PassengerAccountMapper : Mapper<PassengerAccountModel, PassengerAccount> {
    private val profileMapper = get<ProfileMapper>()

    override fun toView(type: PassengerAccount) =
        PassengerAccountModel(
            id       = type.id,
            profile  = profileMapper.toView(type.profile),
            lastSeen = type.lastSeen?.let { SystemUtils.formatDateTime(it) }
        )

    override fun fromView(type: PassengerAccountModel): PassengerAccount { throw UnsupportedOperationException() }
}
