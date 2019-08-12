package com.kg.gettransfer.sys.domain

class SetAccessTokenInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: String) {
        repository.getResult().getModel().copy(accessToken = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
