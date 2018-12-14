package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Representation for a [UserEntity] fetched from an external layer data source.
 */
@Serializable
data class UserEntity(
    @SerialName(PROFILE) val profile: ProfileEntity,
    @SerialName(TERMS_ACCEPTED) val termsAccepted: Boolean = true
) {

    companion object {
        const val PROFILE        = "profile"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}
