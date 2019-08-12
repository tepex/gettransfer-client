package com.kg.gettransfer.sys.domain

class SetEndpointInteractor(
    private val repository: PreferencesRepository,
    private val endpointRepository: EndpointRepository
) {

    /**
     * Save new endpoint value and initialize remote.
     */
    suspend operator fun invoke(endpoint: Endpoint) {
        repository.getResult().getModel().copy(endpoint = endpoint).also { repository.put(it) }
        endpointRepository.put(endpoint.url)
    }
}
