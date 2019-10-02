package com.kg.gettransfer.sys.domain

class SetIpApiKeyInteractor(
    private val ipApiKeyRepository: IpApiKeyRepository
) {

    /**
     * Initialize ipApiKey value in remote.
     */
    suspend operator fun invoke(key: String) {
        ipApiKeyRepository.put(key)
    }
}
