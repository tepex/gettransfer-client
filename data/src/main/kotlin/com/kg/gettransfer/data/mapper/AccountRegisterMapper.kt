package com.kg.gettransfer.data.mapper

import com.kg.gettransfer.data.model.RegistrationAccountEntity
import com.kg.gettransfer.domain.model.RegistrationAccount

/**
 * Map a [RegistrationAccount] (data) to and from a [RegistrationAccountEntity] (domain) instance when data is moving between
 * this later and the Domain layer
 *
 * @author П. Густокашин (Diwixis)
 */
open class AccountRegisterMapper : Mapper<RegistrationAccountEntity, RegistrationAccount> {
    override fun fromEntity(type: RegistrationAccountEntity): RegistrationAccount {
        return type.let { RegistrationAccount(it.email!!, it.phone!!, it.termsAccepted, it.fullName) }
    }

    override fun toEntity(type: RegistrationAccount): RegistrationAccountEntity {
        return RegistrationAccountEntity(type.fullName, type.email, type.phone, type.termsAccepted)
    }
}