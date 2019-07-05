package com.kg.gettransfer.domain.model

import java.util.Date

data class PassengerAccount(
    override val id: Long,
    val profile: Profile,
    val lastSeen: Date?
) : Entity {

    companion object {
        val EMPTY = PassengerAccount(0, Profile.EMPTY.copy(), null)
    }
}
