package com.kg.gettransfer.sys.domain

class SetAppEntersInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Int) {
        repository.getResult().getModel().copy(appEnters = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
