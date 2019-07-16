package com.kg.gettransfer.sys.domain

import com.kg.gettransfer.core.domain.Repository

class GetTermsUrlInteractor(
    private val repository: Repository<MobileConfigs>
) {

    operator fun invoke() = repository.get().termsUrl
}
