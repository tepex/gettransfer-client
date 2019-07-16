package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.PassengerAccount
import java.text.DateFormat

data class PassengerAccountEntity(
    val id: Long,
    val profile: ProfileEntity,
    var lastSeen: String?
) {

    companion object {
        const val ID        = "id"
        const val PROFILE   = "profile"
        const val LAST_SEEN = "last_seen"
    }
}

fun PassengerAccount.map(dateFormat: DateFormat) =
    PassengerAccountEntity(id, profile.map(), lastSeen?.let { dateFormat.format(it) })

fun PassengerAccountEntity.map(dateFormat: DateFormat) =
    PassengerAccount(id, profile.map(), lastSeen?.let { dateFormat.parse(lastSeen) })
