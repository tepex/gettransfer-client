package com.kg.gettransfer.sys.domain

class SetLastModeInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: String) {
        repository.getResult().getModel().copy(lastMode = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
