package com.kg.gettransfer.data.model

/**
 * Representation for a [UserEntity] fetched from an external layer data source.
 */
data class UserEntity(var name: String?,
                      var email: String?,
                      var phone: String?,
                      var termsAccepted: Boolean = true)
