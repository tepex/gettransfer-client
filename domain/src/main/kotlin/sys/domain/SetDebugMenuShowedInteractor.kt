package com.kg.gettransfer.sys.domain

class SetDebugMenuShowedInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(isDebugMenuShowed = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
