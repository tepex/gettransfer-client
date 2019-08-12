package com.kg.gettransfer.sys.domain

class SetFirstLaunchInteractor(
    private val repository: PreferencesRepository
) {

    suspend operator fun invoke(value: Boolean) {
        repository.getResult().getModel().copy(isFirstLaunch = value).also { newPreferences ->
            repository.put(newPreferences)
        }
    }
}
