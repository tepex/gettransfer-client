package com.kg.gettransfer.data.model

import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.User

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Representation for a [UserEntity] fetched from an external layer data source.
 */
@Serializable
data class UserEntity(
    @SerialName(PROFILE) val profile: ProfileEntity,
    @SerialName(TERMS_ACCEPTED) val termsAccepted: Boolean
) {

    companion object {
        const val PROFILE        = "profile"
        const val TERMS_ACCEPTED = "terms_accepted"
    }
}

fun User.map() = UserEntity(profile.map(), termsAccepted)
fun UserEntity.map(): User {
    val pr = profile.map()
    return if (pr === Profile.EMPTY) User.EMPTY else User(pr, termsAccepted)
}
