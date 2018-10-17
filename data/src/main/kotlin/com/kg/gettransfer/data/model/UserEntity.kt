package com.kg.gettransfer.data.model

/**
 * Representation for a [UserEntity] fetched from an external layer data source.
 */
data class UserEntity(val profile: ProfileEntity,
                      val termsAccepted: Boolean = true)
