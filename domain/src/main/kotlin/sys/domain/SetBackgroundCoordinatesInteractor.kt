package com.kg.gettransfer.sys.domain

class SetBackgroundCoordinatesInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(backgroundCoordinatesAccepted = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
