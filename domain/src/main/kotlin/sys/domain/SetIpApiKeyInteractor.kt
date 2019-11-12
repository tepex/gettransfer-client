package com.kg.gettransfer.sys.domain

class SetIpApiKeyInteractor(
    private val repository: PreferencesRepository,
    private val ipApiKeyRepository: IpApiKeyRepository
) {

    /**
     * Save ipApiKey value and initialize remote.
     */
    suspend operator fun invoke(key: String) {
        repository.getResult().getModel().copy(ipApiKey = key).also { repository.put(it) }
        ipApiKeyRepository.put(key)
    }
}
