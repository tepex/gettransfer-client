package com.kg.gettransfer.domain.model

data class Profile(
    override val id: Long,
    var fullName: String?,
    var email: String?,
    var phone: String?
) : Entity()
